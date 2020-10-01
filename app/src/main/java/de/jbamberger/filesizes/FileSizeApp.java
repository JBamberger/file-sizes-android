package de.jbamberger.filesizes;

import timber.log.Timber;

public class FileSizeApp extends android.app.Application {

    ResourceLocator resourceLocator;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        resourceLocator = new ResourceLocator();
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }
}
