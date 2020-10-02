package de.jbamberger.filesizes;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

class FilesViewHolder extends RecyclerView.ViewHolder {
    private FilesAdapter filesAdapter;
    private Context appContext;
    private ConstraintLayout itemView;
    private TextView fileName;
    private TextView fileInfo;
    private ImageView icon;
    private ProgressBar spaceUsage;

    FilesViewHolder(FilesAdapter filesAdapter, @NonNull ConstraintLayout itemView) {
        super(itemView);
        this.filesAdapter = filesAdapter;
        this.appContext = itemView.getContext().getApplicationContext();
        this.itemView = itemView;
        this.fileName = itemView.findViewById(R.id.file_name);
        this.fileInfo = itemView.findViewById(R.id.file_info);
        this.icon = itemView.findViewById(R.id.file_icon);
        this.spaceUsage = itemView.findViewById(R.id.space_usage);
    }

    void bind(Item item) {
        this.fileName.setText(item.name);
        this.icon.setImageResource(FileUtils.getIconForItem(item.type));

        if (item.type == ItemType.FOLDER) {
            this.itemView.setOnClickListener(view -> filesAdapter.selectItem(item));
            this.fileInfo.setText(createItemDescription(item));
        } else {
            this.fileInfo.setText(StringUtils.formatHRByteCount(item.size, false));
            this.itemView.setOnClickListener(view -> {
                final Intent intent = AndroidUtils.buildFileOpenIntent(appContext, item.source);
                try {
                    itemView.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(itemView.getContext(), "Could not open file!", Toast.LENGTH_LONG).show();
                }
            });
        }
        spaceUsage.setMax(100);
        if (item.parent == null) {
            spaceUsage.setProgress(100);
        } else {
            spaceUsage.setProgress((int) (((double) item.totalSize / (double) item.parent.totalSize) * 100d));
        }
    }

    @NotNull
    private String createItemDescription(Item item) {
        String formattedSize = StringUtils.formatHRByteCount(item.totalSize, false);
        return String.format(Locale.ROOT, "%d Children | %d Files | %d Folders | %s",
                item.children.size(), item.fileCount, item.folderCount, formattedSize);
    }

}
