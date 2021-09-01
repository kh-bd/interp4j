package com.github.interp4j.processor.s;

import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

import java.util.Optional;

/**
 * 'S' expression parser.
 *
 * @author Sergei_Khadanovich
 */
class SExpressionParser {

    private final GrammarParser parser = new GrammarParser(new SGrammarDefinition());

    /**
     * Parse supplied string literal to {@link SExpression}.
     *
     * @param literal string literal
     * @return parsed {@link SExpression} or empty if parsing was failed
     */
    Optional<SExpression> parse(String literal) {
        Result result = parser.parse(literal);
        if (result.isFailure()) {
            return Optional.empty();
        }
        return Optional.of(result.get());
    }
}
