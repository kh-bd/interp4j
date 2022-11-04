package dev.khbd.interp4j.javac.plugin.s.expr;

/**
 * @author Sergei_Khadanovich
 */
public record ExpressionPart(String expression, int start, int end) implements SExpressionPart {

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.EXPRESSION;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitExpressionPart(this);
    }
}
