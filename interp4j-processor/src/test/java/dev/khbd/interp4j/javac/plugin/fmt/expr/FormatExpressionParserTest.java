package dev.khbd.interp4j.javac.plugin.fmt.expr;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.fmt.FormatExpression;
import dev.khbd.interp4j.javac.plugin.fmt.FormatExpressionParser;
import dev.khbd.interp4j.javac.plugin.fmt.FormatText;
import dev.khbd.interp4j.javac.plugin.fmt.Position;
import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei Khadanovich
 */
public class FormatExpressionParserTest {

    private static final FormatExpressionParser PARSER = FormatExpressionParser.getInstance();

    @Test
    public void parse_thereIsNoExpressions_returnValidSExpression() {
        Optional<FormatExpression> expression = PARSER.parse("Hello world!!!");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello world!!!", new Position(0, 14)))
                        .build()
        );
    }
}