package dev.khbd.interp4j.javac.plugin.s.expr;

import lombok.Value;

/**
 * Code model.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class SCode implements SExpressionPart {

    String expression;
    int start;
    int end;

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.CODE;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitCodePart(this);
    }
}
