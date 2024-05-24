package dev.khbd.interp4j.javac.plugin.s;

/**
 * Text part model.
 *
 * @author Sergei_Khadanovich
 */
public record SText(String text, Position position) implements SExpressionPart {

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.TEXT;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
