package dev.khbd.interp4j.processor.s.expr;

import lombok.NonNull;
import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

import java.util.Optional;

/**
 * 'S' expression parser.
 *
 * @author Sergei_Khadanovich
 */
public final class SExpressionParser {

    private static final SExpressionParser INSTANCE = new SExpressionParser();

    private SExpressionParser() {
    }

    private final GrammarParser parser = new GrammarParser(new SGrammarDefinition());

    /**
     * Parse supplied string literal to {@link SExpression}.
     *
     * @param literal string literal
     * @return parsed {@link SExpression} or empty if parsing was failed
     */
    public Optional<SExpression> parse(@NonNull String literal) {
        Result result = parser.parse(literal);
        if (result.isFailure()) {
            return Optional.empty();
        }
        return Optional.of(result.get());
    }

    public static SExpressionParser getInstance() {
        return INSTANCE;
    }
}
