package de.jbamberger.filesizes;

import java.util.Locale;

public final class StringUtils {
    private StringUtils() {
        throw new AssertionError("No instances allowed!");
    }

    /**
     * Formats a count of bytes as a human readable number using either SI units or base 2^10 units.
     *
     * @param bytes number of bytes
     * @param si    true to use SI units
     * @return formatted byte count
     * @link https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     */
    public static String formatHRByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ROOT, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
