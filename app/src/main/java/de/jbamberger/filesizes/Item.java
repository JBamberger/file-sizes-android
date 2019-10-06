package de.jbamberger.filesizes;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Item {
    @Nullable
    final Item parent;
    final File source;
    final String name;
    final long size;
    final ItemType type;

    final List<Item> children;
    final long totalSize;
    final long fileCount;
    final long folderCount;

    Item(@Nullable Item parent, File source) {
        this.parent = parent;
        this.source = source;
        this.name = source.getName();
        this.size = source.length();

        ItemType t;
        try {
            if (FileUtils.isSymlink(source)) {
                t = ItemType.SYM_LINK;
            } else if (source.isFile()) {
                t = ItemType.FILE;
            } else if (source.isDirectory()) {
                t = ItemType.FOLDER;
            } else {
                t = ItemType.OTHER;
            }
        } catch (IOException e) {
            t = ItemType.OTHER;
        }
        this.type = t;


        if (this.type == ItemType.FOLDER) {
            long sizeSum = this.size;
            long folderAcc = 1;
            long fileAcc = 0;
            final File[] subFiles = source.listFiles();
            if (subFiles == null) {
                this.children = Collections.emptyList();
            } else {
                this.children = new ArrayList<>(subFiles.length);
                for (File subFile : subFiles) {
                    Item child = new Item(this, subFile);
                    this.children.add(child);

                    sizeSum += child.totalSize;
                    fileAcc += child.fileCount;
                    folderAcc += child.folderCount;
                }
                Collections.sort(children, (a, b) -> -Long.compare(a.totalSize, b.totalSize));
            }

            this.totalSize = sizeSum;
            this.fileCount = fileAcc;
            this.folderCount = folderAcc;
        } else {
            this.totalSize = this.size;
            this.fileCount = 1;
            this.folderCount = 0;
            this.children = Collections.emptyList();
        }
    }
}
