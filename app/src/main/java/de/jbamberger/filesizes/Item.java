package de.jbamberger.filesizes;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Item {
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
