package dev.khbd.interp4j.javac.plugin.s.expr;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class SExpressionParserTest {

    private final SExpressionParser parser = SExpressionParser.getInstance();

    @Test
    public void parse_thereIsOnExpression_returnResult() {
        Optional<SExpression> sExpression = parser.parse("Hello ${name1}. ${name2}. How are you?");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText("Hello ", 0, 6))
                        .addPart(new SCode("name1", 8, 13))
                        .addPart(new SText(". ", 14, 16))
                        .addPart(new SCode("name2", 18, 23))
                        .addPart(new SText(". How are you?", 24, 38))
        );
    }

    @Test
    public void parse_dollarEscaped_returnResult() {
        Optional<SExpression> sExpression = parser.parse(" $${name2} ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText(" ${name2} ", 0, 11))
        );
    }

    @Test
    public void parse_dollarEscapedAndExpressionNext_returnResult() {
        Optional<SExpression> sExpression = parser.parse(" $$${name2} ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText(" $", 0, 3))
                        .addPart(new SCode("name2", 5, 10))
                        .addPart(new SText(" ", 11, 12))
        );
    }

    @Test
    public void parse_bothExpressionsDollarEscaped_returnResult() {
        Optional<SExpression> sExpression = parser.parse("$${name1}$${name2}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText("${name1}${name2}", 0, 18))
        );
    }

    @Test
    public void parse_firstEscapedBySecondNot_returnResult() {
        Optional<SExpression> sExpression = parser.parse("$${name1}${name2}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText("${name1}", 0, 9))
                        .addPart(new SCode("name2", 11, 16))
        );
    }

    @Test
    public void parse_firstNotEscapedBySecondDoes_returnResult() {
        Optional<SExpression> sExpression = parser.parse("${name1}$${name2}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SCode("name1", 2, 7))
                        .addPart(new SText("${name2}", 8, 17))
        );
    }

    @Test
    public void parse_thereIsNoExpressions_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("Hello world!!!");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText("Hello world!!!", 0, 14))
        );
    }

    @Test
    public void parse_stringIsEmpty_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("");

        assertThat(sExpression).hasValue(new SExpression());
    }

    @Test
    public void parse_expressionWithoutOtherTest_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("${name}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SCode("name", 2, 6))
        );
    }

    @Test
    public void parse_severalExpressionWithoutTextBetween_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("${name}${age}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SCode("name", 2, 6))
                        .addPart(new SCode("age", 9, 12))
        );
    }

    @Test
    public void parse_expressionContainsBracketInside_returnValidExpression() {
        Optional<SExpression> sExpression = parser.parse(" ${{{name} ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText(" ", 0, 1))
                        .addPart(new SCode("{{name", 3, 9))
                        .addPart(new SText(" ", 10, 11))
        );
    }

    @Test
    public void parse_thereIsNoCloseBracket_failParsing() {
        Optional<SExpression> result = parser.parse("${name");

        assertThat(result).isEmpty();
    }

    @Test
    public void parse_thereIsNoOpenBracket_failParsing() {
        Optional<SExpression> result = parser.parse("$}} name");

        assertThat(result).isEmpty();
    }

    @Test
    public void parse_simpleExpressionWithoutBrackets_parseIt() {
        Optional<SExpression> sExpression = parser.parse(" $name ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new SText(" ", 0, 1))
                        .addPart(new SCode("name", 2, 6))
                        .addPart(new SText(" ", 6, 7))
        );
    }

    @Test
    public void parse_thereIsNoBrackets_parseIt() {
        Optional<SExpression> result = parser.parse("$name");

        assertThat(result).hasValue(
                new SExpression()
                        .addPart(new SCode("name", 1, 5))
        );
    }

    @Test
    public void parse_thereIsNoBracketsAndExpressionNotSimple_parseIt() {
        Optional<SExpression> result = parser.parse("$name.value");

        assertThat(result).hasValue(
                new SExpression()
                        .addPart(new SCode("name", 1, 5))
                        .addPart(new SText(".value", 5, 11))
        );
    }
}
