package de.jbamberger.filesizes;

public final class Constants {


    private Constants() {
        throw new AssertionError("No instances allowed!");
    }

    public static final String FILE_PROVIDER_AUTHORITY = "de.jbamberger.filesizes.fileprovider";

    static final int ACTION_REQUEST_STORAGE_PERMISSIONS = 28542;
    static final int ACTION_REQUEST_MANAGE_STORAGE_PERMISSIONS = 28543;
}
