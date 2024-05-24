package dev.khbd.interp4j.javac.plugin.s.expr;

/**
 * Code model.
 *
 * @author Sergei_Khadanovich
 */
public record SCode(String expression, int start, int end) implements SExpressionPart {

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.CODE;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitExpressionPart(this);
    }
}
