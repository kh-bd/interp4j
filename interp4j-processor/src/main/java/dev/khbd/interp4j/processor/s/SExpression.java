package dev.khbd.interp4j.processor.s;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsed 's' expression.
 *
 * @author Sergei_Khadanovich
 */
@Getter(AccessLevel.PACKAGE)
@ToString
@EqualsAndHashCode
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
}
