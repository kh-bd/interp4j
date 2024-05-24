package dev.khbd.interp4j.javac.plugin.fmt.expr;

import lombok.NonNull;
import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

import java.util.Optional;

/**
 * 'fmt' expression parser.
 *
 * @author Sergei_Khadanovich
 */
public final class FormatExpressionParser {

    private static final FormatExpressionParser INSTANCE = new FormatExpressionParser();

    private FormatExpressionParser() {
    }

    private final GrammarParser parser = new GrammarParser(new FormatGrammarDefinition());

    /**
     * Parse supplied string literal to {@link FormatExpression}.
     *
     * @param literal string literal
     * @return parsed {@link FormatExpression} or empty if parsing was failed
     */
    public Optional<FormatExpression> parse(@NonNull String literal) {
        Result result = parser.parse(literal);
        if (result.isFailure()) {
            return Optional.empty();
        }
        return Optional.of(result.get());
    }

    public static FormatExpressionParser getInstance() {
        return INSTANCE;
    }
}
