package dev.khbd.interp4j.processor.s.expr;

/**
 * 's' expression visitor.
 *
 * @author Sergei_Khadanovich
 */
public interface SExpressionVisitor {

    default void start() {
    }

    default void visitTextPart(TextPart textPart) {
    }

    default void visitExpressionPart(ExpressionPart expressionPart) {
    }

    default void finish() {
    }
}
