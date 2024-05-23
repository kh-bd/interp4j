package dev.khbd.interp4j.core;

/**
 * Entry point to string interpolation.
 *
 * <p>To use string interpolation simply invoke {@link #s(String)} method
 * with string literal. String literal can contain java expressions.
 * For example,
 *
 * <pre>{@code
 * String name = "Alex";
 * String greet = Interpolations.s("Hello ${name}, how are you?");
 *
 * assert greet.equals("Hello Alex, how are you?");
 * }</pre>
 *
 * @author Sergei_Khadanovich
 */
public final class Interpolations {

    private Interpolations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Interpolate specified string literal.
     *
     * @param str string literal
     * @return interpolated string
     */
    public static String s(String str) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    /**
     * Interpolate specified string literal.
     *
     * @param str string literal
     * @return interpolated string
     */
    public static String fmt(String str) {
        throw new UnsupportedOperationException("Operation is not supported");
    }
}
