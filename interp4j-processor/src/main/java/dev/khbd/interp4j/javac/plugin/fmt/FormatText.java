package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.Value;

/**
 * Text block in format expression.
 *
 * @author Sergei Khadanovich
 */
@Value
public class FormatText implements FormatExpressionPart {

    String text;
    Position position;

    @Override
    public FormatExpressionPartKind kind() {
        return FormatExpressionPartKind.TEXT;
    }

    @Override
    public void visit(FormatExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
