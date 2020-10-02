package de.jbamberger.filesizes;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FileSelectionFragment extends DialogFragment {

    private static class FileViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemView;
        TextView fileName;
        TextView fileInfo;
        ImageView icon;
        ProgressBar spaceUsage;

        public FileViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            this.itemView = itemView;
            this.fileName = itemView.findViewById(R.id.file_name);
            this.fileInfo = itemView.findViewById(R.id.file_info);
            this.icon = itemView.findViewById(R.id.file_icon);
            this.spaceUsage = itemView.findViewById(R.id.space_usage);
        }
    }

    private static class FilesAdapter extends RecyclerView.Adapter<FileViewHolder> {

        FileSelectionFragment fragment;
        List<File> listing;

        public FilesAdapter(FileSelectionFragment fragment) {
            this.fragment = fragment;
            this.listing = Collections.emptyList();
        }

        public void setListing(List<File> listing) {
            this.listing = listing;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file, parent, false);
            return new FileViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            final File file = this.listing.get(position);
            holder.fileName.setText(file.toString());
            holder.itemView.setOnClickListener(view -> {
                fragment.selectFile(file);
            });
        }

        @Override
        public int getItemCount() {
            return listing.size();
        }
    }

    private FilesAdapter adapter = new FilesAdapter(this);
    private FileSelectionViewModel viewModel;

    public static FileSelectionFragment newInstance() {
        return new FileSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.file_selection_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView fileList = view.findViewById(R.id.file_list);
        fileList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        fileList.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FileSelectionViewModel.class);

        viewModel.getFiles().observe(getViewLifecycleOwner(), (dirListing) -> {
            if (dirListing != null) {
                adapter.setListing(dirListing.children);
                // TODO: show current dir in titlebar or similar

            } else {
                adapter.setListing(Collections.emptyList());
            }
        });
        viewModel.selectFile(Environment.getExternalStorageDirectory());
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getDialog()
                .getWindow()
                .setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
    }

    void selectFile(File file) {
        if (file.isDirectory()) {
            viewModel.selectFile(file);
        } else {
            // TODO
        }
    }

}