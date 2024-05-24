package dev.khbd.interp4j.javac.plugin.s;

/**
 * Code model.
 *
 * @author Sergei_Khadanovich
 */
public record SCode(String expression, Position position) implements SExpressionPart {

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.CODE;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitExpressionPart(this);
    }
}
