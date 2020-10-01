package de.jbamberger.filesizes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

import timber.log.Timber;

public class AndroidUtils {

    private AndroidUtils() {
        throw new AssertionError("No instances allowed!");
    }

    public static Intent buildFileOpenIntent(Context appContext, File path) {
        String mime = FileUtils.getMimeType(appContext, Uri.fromFile(path));
        if (mime == null) {
            mime = "*/*";
        }

        Uri uri = FileProvider.getUriForFile(appContext, Constants.FILE_PROVIDER_AUTHORITY, path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }
}
