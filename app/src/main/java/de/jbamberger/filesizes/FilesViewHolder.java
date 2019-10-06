package de.jbamberger.filesizes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

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

        if (item.type == ItemType.FOLDER) {
            this.itemView.setOnClickListener(view -> filesAdapter.selectItem(item));
            this.icon.setImageResource(R.drawable.ic_folder_24dp);
            this.fileInfo.setText((item.children.size() == 0 ? "Empty | " : item.children.size() + " Files | ") + StringUtils.formatHRByteCount(item.totalSize, true));
        } else {
            if (item.type == ItemType.FILE) {
                this.icon.setImageResource(R.drawable.ic_file_24dp);
            } else if (item.type == ItemType.SYM_LINK) {
                this.icon.setImageResource(R.drawable.ic_link_24dp);
            } else {
                this.icon.setImageResource(R.drawable.ic_broken_image_24dp);
            }
            this.fileInfo.setText(StringUtils.formatHRByteCount(item.size, true));

            this.itemView.setOnClickListener(view -> {
                String mime = FileUtils.getMimeType(appContext, Uri.fromFile(item.source));
                if (mime == null) {
                    mime = "*/*";
                }
                Timber.d("Detected mime type '%s'", mime);

                Uri uri = FileProvider.getUriForFile(
                        appContext, Constants.FILE_PROVIDER_AUTHORITY, item.source);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mime);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                itemView.getContext().startActivity(intent);
            });


        }
        spaceUsage.setMax(100);
        if (item.parent == null) {
            spaceUsage.setProgress(100);
        } else {
            spaceUsage.setProgress((int) (((double) item.totalSize / (double) item.parent.totalSize) * 100d));
        }
    }
}
