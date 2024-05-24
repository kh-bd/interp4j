package dev.khbd.interp4j.javac.plugin.s.expr;

import lombok.Value;

/**
 * Text part model.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class SText implements SExpressionPart {

    String text;
    int start;
    int end;

    @Override
    public SExpressionPartKind kind() {
        return SExpressionPartKind.TEXT;
    }

    @Override
    public void visit(SExpressionVisitor visitor) {
        visitor.visitTextPart(this);
    }
}
