package de.jbamberger.filesizes;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class FileInfoProvider {

    interface ItemResultCallback {
        void onResult(Item item);
    }

    private ExecutorService executor;
    private Handler uiThread;

    public FileInfoProvider() {
        this.executor = Executors.newSingleThreadExecutor();
        this.uiThread = new Handler(Looper.getMainLooper());
    }

    public void loadInfo(File root, ItemResultCallback callback) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("Executor already terminated!");
        }

        // file system operations are scheduled on a background thread
        executor.submit(() -> {
            // this constructor performs the heavy work of traversing the file system.
            Item item = new Item(null, root);

            // Post the result on the ui thread to display it.
            uiThread.post(() -> callback.onResult(item));
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    Timber.e("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
