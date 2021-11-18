package dev.khbd.interp4j.processor.s.expr;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class ExpressionPart implements SExpressionPart {
    String expression;
    int startPosition;

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.EXPRESSION;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitExpressionPart(this);
    }
}
