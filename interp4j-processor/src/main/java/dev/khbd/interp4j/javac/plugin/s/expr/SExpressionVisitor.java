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
     * @param textPart text part
     */
    default void visitTextPart(TextPart textPart) {
    }

    /**
     * Visit expression part.
     *
     * @param expressionPart expression part
     */
    default void visitExpressionPart(ExpressionPart expressionPart) {
    }

    /**
     * Finish traversing.
     */
    default void finish() {
    }
}
