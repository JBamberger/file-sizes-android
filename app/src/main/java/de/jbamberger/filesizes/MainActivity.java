package de.jbamberger.filesizes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 28542;

    FilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter.navUp()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE
                && permissions.length == 1
                && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(this, "Could not get permissions!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init() {
        RecyclerView rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Item rootItem = new Item(null, Environment.getExternalStorageDirectory());
        this.adapter = new FilesAdapter(getSupportActionBar(), rootItem);
        rv.setAdapter(adapter);
    }

    private static class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

        private final ActionBar actionBar;
        private Item item;

        FilesAdapter(ActionBar actionBar, Item item) {
            this.actionBar = actionBar;
            selectItem(item);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file, parent, false);

            return new ViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(item.children.get(position));
        }

        @Override
        public int getItemCount() {
            return item.children.size();
        }

        private void selectItem(Item item) {
            this.item = item;
            actionBar.setSubtitle(item.source.getAbsolutePath());
            notifyDataSetChanged();
        }

        public boolean navUp() {
            if (item.parent != null) {
                selectItem(item.parent);
                return true;
            }
            return false;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            ConstraintLayout itemView;
            TextView fileName;
            TextView fileInfo;
            ImageView icon;
            ProgressBar spaceUsage;

            ViewHolder(@NonNull ConstraintLayout itemView) {
                super(itemView);
                this.itemView = itemView;
                this.fileName = itemView.findViewById(R.id.file_name);
                this.fileInfo = itemView.findViewById(R.id.file_info);
                this.icon = itemView.findViewById(R.id.file_icon);
                this.spaceUsage = itemView.findViewById(R.id.space_usage);
            }

            void bind(Item item) {
                itemView.setOnClickListener(view -> selectItem(item));
                this.fileName.setText(item.name);

                if (item.type == ItemType.FOLDER) {
                    this.icon.setImageResource(R.drawable.ic_folder_24dp);
                    this.fileInfo.setText((item.children.size() == 0 ? "Empty | " : item.children.size() + " Files | ") + formatSize(item.totalSize));
                } else {
                    if (item.type == ItemType.FILE) {
                        this.icon.setImageResource(R.drawable.ic_file_24dp);
                    } else {
                        this.icon.setImageResource(R.drawable.ic_broken_image_24dp);
                    }
                    this.fileInfo.setText(formatSize(item.size));
                }
                spaceUsage.setMax(100);
                if (item.parent == null) {
                    spaceUsage.setProgress(100);
                } else {
                    spaceUsage.setProgress((int) (((double) item.totalSize / (double) item.parent.totalSize) * 100d));
                }
            }

            private static final long KIB_SIZE = 1L << 10;
            private static final long MIB_SIZE = 1L << 20;
            private static final long GIB_SIZE = 1L << 30;
            private static final long TIB_SIZE = 1L << 40;

            private String formatSize(long size) {
                double s;
                String end;
                if (size < KIB_SIZE) { // Bytes
                    s = size;
                    end = "B";
                } else if (size < MIB_SIZE) { // kiB
                    s = (double) size / KIB_SIZE;
                    end = "kiB";
                } else if (size < GIB_SIZE) { // MiB
                    s = (double) size / MIB_SIZE;
                    end = "MiB";
                } else if (size < TIB_SIZE) { // GiB
                    s = (double) size / GIB_SIZE;
                    end = "GiB";
                } else {
                    s = (double) size / TIB_SIZE;
                    end = "TiB";
                }
                DecimalFormat df = new DecimalFormat("#0.##");
                return df.format(s) + end;
            }
        }
    }

    private static final class Item {
        @Nullable
        Item parent;
        File source;
        String name;
        long size;
        ItemType type;

        List<Item> children;
        long totalSize;

        Item(@Nullable Item parent, File source) {
            this.parent = parent;
            this.source = source;
            this.name = source.getName();
            this.size = source.length();
            if (source.isFile()) {
                this.type = ItemType.FILE;
            } else if (source.isDirectory()) {
                this.type = ItemType.FOLDER;
            } else {
                this.type = ItemType.OTHER;
            }

            this.totalSize = this.size;

            final File[] subFiles = source.listFiles();
            if (subFiles == null) {
                this.children = Collections.emptyList();
            } else {
                this.children = new ArrayList<>(subFiles.length);
                for (File subFile : subFiles) {
                    Item child = new Item(this, subFile);
                    this.children.add(child);
                    this.totalSize += child.totalSize;
                }
                Collections.sort(children, (a, b) -> -Long.compare(a.totalSize, b.totalSize));

            }
        }
    }

    private enum ItemType {
        FILE, FOLDER, OTHER
    }
}
