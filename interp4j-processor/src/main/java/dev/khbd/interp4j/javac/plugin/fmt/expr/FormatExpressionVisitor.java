package dev.khbd.interp4j.javac.plugin.fmt.expr;

/**
 * 'fmt' expression visitor.
 *
 * @author Sergei_Khadanovich
 */
public interface FormatExpressionVisitor {

    /**
     * Start traversing.
     */
    default void start() {
    }

    /**
     * Visit text part.
     *
     * @param text text part
     */
    default void visitTextPart(FormatText text) {
    }

    /**
     * Visit code part.
     *
     * @param code code part
     */
    default void visitCodePart(FormatCode code) {
    }

    /**
     * Visit specifier part.
     *
     * @param specifier specifier part
     */
    default void visitSpecifierPart(FormatSpecifier specifier) {
    }

    /**
     * Finish traversing.
     */
    default void finish() {
    }
}
