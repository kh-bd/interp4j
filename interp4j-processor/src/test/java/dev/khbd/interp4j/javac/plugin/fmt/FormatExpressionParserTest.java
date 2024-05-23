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

    @Test
    public void parse_specifierAndCodeAreSeparated_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("Hello! My name is %d ${name}");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FormatSpecifier(new Conversion("d"), new Position(18, 20)))
                        .text(new FormatText(" ", new Position(20, 21)))
                        .code(new FormatCode("name", new Position(23, 27)))
                        .build()
        );
    }

    @Test
    public void parse_withExplicitPosition_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("Hello! My name is %1$s${name}. Age is %2$d${age}");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FormatSpecifier(new NumericIndex(1), new Conversion("s"), new Position(18, 22)))
                        .code(new FormatCode("name", new Position(24, 28)))
                        .text(new FormatText(". Age is ", new Position(29, 38)))
                        .specifier(new FormatSpecifier(new NumericIndex(2), new Conversion("d"), new Position(38, 42)))
                        .code(new FormatCode("age", new Position(44, 47)))
                        .build()
        );
    }

    @Test
    public void parse_withImplicitPosition_returnParsed() {
        Optional<FormatExpression> expression = PARSER.parse("Hello! My name is %1$s${name}. Age is %<d${age}");

        assertThat(expression).hasValue(
                FormatExpression.builder()
                        .text(new FormatText("Hello! My name is ", new Position(0, 18)))
                        .specifier(new FormatSpecifier(new NumericIndex(1), new Conversion("s"), new Position(18, 22)))
                        .code(new FormatCode("name", new Position(24, 28)))
                        .text(new FormatText(". Age is ", new Position(29, 38)))
                        .specifier(new FormatSpecifier(new ImplicitIndex(), new Conversion("d"), new Position(38, 41)))
                        .code(new FormatCode("age", new Position(43, 46)))
                        .build()
        );
    }
}