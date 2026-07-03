package com.automation.framework.utilities;

/**
 * Small string helpers used across the framework. For richer operations prefer
 * Apache Commons Lang {@code org.apache.commons.lang3.StringUtils}.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    /** Collapse runs of whitespace and trim - handy for normalizing page text. */
    public static String normalizeSpace(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    public static String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max) + "...";
    }
}
