package de.jbamberger.filesizes;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

class FilesAdapter extends RecyclerView.Adapter<FilesViewHolder> {

    private final ActionBar actionBar;
    private Item item;

    FilesAdapter(ActionBar actionBar, Item item) {
        this.actionBar = actionBar;
        selectItem(item);
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file, parent, false);

        return new FilesViewHolder(this, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        holder.bind(item.children.get(position));
    }

    @Override
    public int getItemCount() {
        return item.children.size();
    }

    void selectItem(Item item) {
        this.item = item;
        actionBar.setSubtitle(item.source.getAbsolutePath());
        notifyDataSetChanged();
    }

    boolean navUp() {
        if (item.parent != null) {
            selectItem(item.parent);
            return true;
        }
        return false;
    }

}
