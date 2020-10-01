package de.jbamberger.filesizes;

public class ResourceLocator {

    private final FileInfoProvider fileInfoProvider;

    public ResourceLocator() {
        fileInfoProvider = new FileInfoProvider();
    }

    public FileInfoProvider getFileInfoProvider() {
        return fileInfoProvider;
    }
}
