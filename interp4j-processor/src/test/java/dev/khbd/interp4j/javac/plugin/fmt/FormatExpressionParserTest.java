package dev.khbd.interp4j.javac.plugin.fmt;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei Khadanovich
 */
public class FormatExpressionParserTest {

    private static final FormatExpressionParser PARSER = FormatExpressionParser.getInstance();

    @Test
    public void parse_specialDoublePercentSpecifier_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("%%");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .specifier(new FormatSpecifier(new Conversion("%"), new Position(0, 2)))
                        .build()
        );
    }

    @Test
    public void parse_specialNSpecifier_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("%n");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .specifier(new FormatSpecifier(new Conversion("n"), new Position(0, 2)))
                        .build()
        );
    }

    @Test
    public void parse_onlyText_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("hello");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("hello", new Position(0, 5)))
                        .build()
        );
    }

    @Test
    public void parse_simpleExpressionWithOneCodeBlockInBrackets_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("Hello! My name is %s${name}");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FormatSpecifier(new Conversion("s"), new Position(18, 20)))
                        .code(new FormatCode("name", new Position(22, 26)))
                        .build()
        );
    }

    @Test
    public void parse_simpleExpressionWithOneCodeBlock_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("Hello! My name is %s$name");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FormatSpecifier(new Conversion("s"), new Position(18, 20)))
                        .code(new FormatCode("name", new Position(21, 25)))
                        .build()
        );
    }
}