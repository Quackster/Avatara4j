package net.h4bbo.avatara4j.extensions;

public final class StringExtensions {
    private StringExtensions() {
        // Prevent instantiation
    }

    public static boolean isNumeric(String theValue) {
        if (theValue == null) {
            return false;
        }
        try {
            Long.parseLong(theValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* Utility methods */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}