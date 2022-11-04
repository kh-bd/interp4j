package dev.khbd.interp4j.javac.plugin.s.expr;

/**
 * @author Sergei_Khadanovich
 */
public record TextPart(String text, int start, int end) implements SExpressionPart {

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.TEXT;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
