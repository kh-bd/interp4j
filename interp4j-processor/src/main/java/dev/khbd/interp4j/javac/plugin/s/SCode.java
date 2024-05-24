package dev.khbd.interp4j.javac.plugin.s;

import lombok.Value;

/**
 * Code model.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class SCode implements SExpressionPart {

    String expression;
    Position position;

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.CODE;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitCodePart(this);
    }
}
