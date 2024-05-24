package dev.khbd.interp4j.javac.plugin.fmt;

/**
 * Text block in format expression.
 *
 * @author Sergei Khadanovich
 */
public record FormatText(String text, Position position) implements FormatExpressionPart {

    @Override
    public FormatExpressionPartKind kind() {
        return FormatExpressionPartKind.TEXT;
    }

    @Override
    public void visit(FormatExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
