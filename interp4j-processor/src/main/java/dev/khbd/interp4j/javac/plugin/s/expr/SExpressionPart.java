package dev.khbd.interp4j.javac.plugin.s.expr;

/**
 * @author Sergei_Khadanovich
 */
public sealed interface SExpressionPart permits TextPart, ExpressionPart {

    /**
     * Get part kind.
     *
     * @return part kind
     */
    SExpressionPartKind kind();

    /**
     * Visit part with supplied visitor.
     *
     * @param visitor visitor
     */
    void visit(SExpressionVisitor visitor);

    /**
     * Is expression part text or not.
     *
     * @return {@literal true} if expression part is text and {@literal false} otherwise
     */
    default boolean isText() {
        return kind() == SExpressionPartKind.TEXT;
    }

    /**
     * Is expression part expression or not.
     *
     * @return {@literal true} if expression part is expression and {@literal false} otherwise
     */
    default boolean isExpression() {
        return kind() == SExpressionPartKind.EXPRESSION;
    }

    /**
     * Try to cast current part to text part.
     *
     * @return text part
     * @throws IllegalStateException if current part is not a text
     */
    default TextPart asText() {
        if (!isText()) {
            throw new IllegalStateException("Not a text part");
        }
        return (TextPart) this;
    }

    /**
     * Try to cast current part to expression part.
     *
     * @return expression part
     * @throws IllegalStateException if currency part is not an expression
     */
    default ExpressionPart asExpression() {
        if (!isExpression()) {
            throw new IllegalStateException("Not an expression part");
        }
        return (ExpressionPart) this;
    }
}
