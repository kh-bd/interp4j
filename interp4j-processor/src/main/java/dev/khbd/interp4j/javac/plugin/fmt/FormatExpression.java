package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Format expression model.
 *
 * @author Sergei Khadanovich
 */
@ToString
@EqualsAndHashCode
public class FormatExpression {

    private final List<FormatExpressionPart> parts;

    public FormatExpression(List<FormatExpressionPart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    /**
     * Visit fmt expression.
     *
     * @param visitor visitor
     */
    public void visit(FormatExpressionVisitor visitor) {
        visitor.start();
        for (FormatExpressionPart part : parts) {
            part.visit(visitor);
        }
        visitor.finish();
    }

    /**
     * Create empty builder.
     *
     * @return builder
     */
    public static FormatExpressionBuilder builder() {
        return new FormatExpressionBuilder();
    }
}
