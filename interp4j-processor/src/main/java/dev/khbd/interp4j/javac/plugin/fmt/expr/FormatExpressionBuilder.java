package dev.khbd.interp4j.javac.plugin.fmt.expr;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Format expression builder.
 *
 * @author Sergei Khadanovich
 */
public class FormatExpressionBuilder {

    private final List<FormatExpressionPart> parts = new ArrayList<>();

    FormatExpressionBuilder() {
    }

    /**
     * Add text part to builder.
     *
     * @param text text part
     */
    public FormatExpressionBuilder text(@NonNull FormatText text) {
        this.parts.add(text);
        return this;
    }

    /**
     * Add code part to builder.
     *
     * @param code code part
     */
    public FormatExpressionBuilder code(@NonNull FormatCode code) {
        this.parts.add(code);
        return this;
    }

    /**
     * Add specifier part to builder.
     *
     * @param specifier specifier part
     */
    public FormatExpressionBuilder specifier(@NonNull FormatSpecifier specifier) {
        this.parts.add(specifier);
        return this;
    }

    /**
     * Create format expression.
     */
    public FormatExpression build() {
        return new FormatExpression(parts);
    }
}
