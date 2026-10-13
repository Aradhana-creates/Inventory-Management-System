package com.ims.common.util;

import java.util.regex.Pattern;

/**
 * Stateless input-validation helpers used by services and unit tests.
 * All methods are {@code static}; this class cannot be instantiated.
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern POSITIVE_INT_PATTERN  = Pattern.compile("^\\d+$");
    private static final Pattern POSITIVE_DEC_PATTERN  =
            Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    private Validator() {}

    /**
     * Returns {@code true} when the string is {@code null}, empty, or
     * contains only whitespace characters.
     *
     * @param s the string to test
     * @return {@code true} if blank/null
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Returns {@code true} when the string matches a simple e-mail pattern.
     *
     * @param email the e-mail address to test
     * @return {@code true} if the format is acceptable
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Returns {@code true} when the string represents a non-negative integer
     * (digits only, no sign).
     *
     * @param s the string to test
     * @return {@code true} if the string is a whole non-negative number
     */
    public static boolean isNonNegativeInt(String s) {
        return s != null && POSITIVE_INT_PATTERN.matcher(s.trim()).matches();
    }

    /**
     * Returns {@code true} when the string represents a positive integer
     * (≥ 1, no sign).
     *
     * @param s the string to test
     * @return {@code true} if the string is a positive whole number
     */
    public static boolean isPositiveInt(String s) {
        if (!isNonNegativeInt(s)) return false;
        try { return Integer.parseInt(s.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    /**
     * Returns {@code true} when the string is a non-negative decimal with up
     * to two decimal places (suitable for currency).
     *
     * @param s the string to test
     * @return {@code true} if the string is a valid non-negative decimal
     */
    public static boolean isNonNegativeDecimal(String s) {
        return s != null && POSITIVE_DEC_PATTERN.matcher(s.trim()).matches();
    }

    /**
     * Returns {@code true} when {@code value} is within the closed interval
     * [{@code min}, {@code max}].
     *
     * @param value the value to check
     * @param min   inclusive lower bound
     * @param max   inclusive upper bound
     * @return {@code true} if the value is in range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Returns {@code true} when the string's trimmed length is at least
     * {@code minLen} characters.
     *
     * @param s      the string to test
     * @param minLen minimum required length (after trim)
     * @return {@code true} if length ≥ minLen
     */
    public static boolean hasMinLength(String s, int minLen) {
        return s != null && s.trim().length() >= minLen;
    }
}
