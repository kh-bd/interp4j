package com.github.interp4j.processor.s;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 'S' parser result.
 *
 * @author Sergei_Khadanovich
 */
@Getter
@ToString
@EqualsAndHashCode
class ParseResult {

    private final List<String> parts = new ArrayList<>();
    private final List<String> expressions = new ArrayList<>();

    ParseResult addPart(String part) {
        parts.add(part);
        return this;
    }

    ParseResult addExpression(String expression) {
        expressions.add(expression);
        return this;
    }
}
