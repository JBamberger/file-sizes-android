package de.jbamberger.filesizes;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.DrawableRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileUtils {

    @Nullable
    public static String getMimeType(@NotNull Context context, @NotNull Uri uri) {
        String mimeType;
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static boolean isSymlink(@NotNull File file) throws IOException {
        // canonicalize everything but the last part of the path
        final File parent = file.getParentFile();
        final File canon = parent == null
                ? file
                : new File(parent.getCanonicalFile(), file.getName());

        // check if the canonicalization of the entire path differs from the previously computed one
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    @DrawableRes
    static int getIconForItem(@NotNull ItemType itemType) {
        if (itemType == ItemType.FOLDER) {
            return R.drawable.ic_folder_24dp;
        } else if (itemType == ItemType.FILE) {
            return R.drawable.ic_file_24dp;
        } else if (itemType == ItemType.SYM_LINK) {
            return R.drawable.ic_link_24dp;
        } else {
            return R.drawable.ic_broken_image_24dp;
        }
    }

    @NotNull
    static ItemType getItemType(@NotNull File source) {
        try {
            if (isSymlink(source)) {
                return ItemType.SYM_LINK;
            } else if (source.isFile()) {
                return ItemType.FILE;
            } else if (source.isDirectory()) {
                return ItemType.FOLDER;
            } else {
                return ItemType.OTHER;
            }
        } catch (SecurityException | IOException e) {
            return ItemType.OTHER;
        }
    }
}
