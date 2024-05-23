package dev.khbd.interp4j.javac.plugin.fmt;

/**
 * Numeric index.
 *
 * @author Sergei_Khadanovich
 */
public record NumericIndex(int position) implements Index {

    @Override
    public String toString() {
        return position + "$";
    }
}
