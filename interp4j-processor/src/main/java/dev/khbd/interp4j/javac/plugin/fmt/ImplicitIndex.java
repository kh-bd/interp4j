package dev.khbd.interp4j.javac.plugin.fmt;

/**
 * Implicit index.
 *
 * @author Sergei_Khadanovich
 */
public record ImplicitIndex() implements Index {

    @Override
    public String toString() {
        return "<";
    }
}
