package dev.khbd.interp4j.processor.s;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parsed 's' expression.
 *
 * @author Sergei_Khadanovich
 */
class SExpression {

    private final List<String> parts = new ArrayList<>();
    private final List<String> expressions = new ArrayList<>();

    SExpression addPart(String part) {
        parts.add(part);
        return this;
    }

    SExpression addExpression(String expression) {
        expressions.add(expression);
        return this;
    }

    SExpression merge(SExpression other) {
        SExpression result = new SExpression();

        result.parts.addAll(parts);
        result.parts.addAll(other.parts);

        result.expressions.addAll(expressions);
        result.expressions.addAll(other.expressions);

        return result;
    }

    List<String> getParts() {
        return parts;
    }

    List<String> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "SExpression{" +
                "parts=" + parts +
                ", expressions=" + expressions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpression that = (SExpression) o;
        return parts.equals(that.parts) && expressions.equals(that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parts, expressions);
    }
}
