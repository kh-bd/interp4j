package dev.khbd.interp4j.processor.s.expr;

import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class TextPart implements SExpressionPart {
    String text;
    int startPosition;

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.TEXT;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
