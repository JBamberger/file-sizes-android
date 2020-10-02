package de.jbamberger.filesizes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSelectionViewModel extends ViewModel {

    public static class DirectoryListing {
        public final File directory;
        public final List<File> children;

        public DirectoryListing(File directory, List<File> children) {
            this.directory = directory;
            this.children = children;
        }
    }

    private final MutableLiveData<DirectoryListing> files = new MutableLiveData<>(null);

    void selectFile(File file) {
        File[] children = file.listFiles();
        List<File> childList = children != null ? Arrays.asList(children) : Collections.emptyList();

        files.postValue(new DirectoryListing(file, childList));
    }

    LiveData<DirectoryListing> getFiles() {
        return files;
    }

}