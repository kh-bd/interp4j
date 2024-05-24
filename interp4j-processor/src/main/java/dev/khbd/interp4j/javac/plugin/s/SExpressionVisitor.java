package dev.khbd.interp4j.javac.plugin.s;

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
     * @param textPart text part
     */
    default void visitTextPart(SText textPart) {
    }

    /**
     * Visit expression part.
     *
     * @param expressionPart expression part
     */
    default void visitExpressionPart(SCode expressionPart) {
    }

    /**
     * Finish traversing.
     */
    default void finish() {
    }
}
