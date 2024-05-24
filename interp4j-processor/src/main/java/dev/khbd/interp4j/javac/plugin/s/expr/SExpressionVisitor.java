package dev.khbd.interp4j.javac.plugin.s.expr;

/**
 * 's' expression visitor.
 *
 * @author Sergei_Khadanovich
 */
public interface SExpressionVisitor {

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
    default void visitTextPart(SText text) {
    }

    /**
     * Visit expression part.
     *
     * @param code expression part
     */
    default void visitCodePart(SCode code) {
    }

    /**
     * Finish traversing.
     */
    default void finish() {
    }
}
