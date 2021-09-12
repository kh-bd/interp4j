package dev.khbd.interp4j.core;

/**
 * @author Sergei_Khadanovich
 */
public class Interpolations {

    private Interpolations() {
    }

    /**
     * Interpolate specified string literal.
     *
     * @param str string literal
     * @return interpolated string
     */
    public static String s(String str) {
        throw new InterpolationNotImplementedException(str);
    }
}
