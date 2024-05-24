package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.Value;

/**
 * Code part in s expression.
 *
 * <p>For example, in fmt("hello, %s$name"), expression part is name.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class FormatCode implements FormatExpressionPart {

    String expression;
    Position position;

    @Override
    public FormatExpressionPartKind kind() {
        return FormatExpressionPartKind.CODE;
    }

    @Override
    public void visit(FormatExpressionVisitor visitor) {
        visitor.visitCodePart(this);
    }
}
