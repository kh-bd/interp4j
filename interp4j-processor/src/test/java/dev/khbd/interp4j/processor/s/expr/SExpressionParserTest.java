package dev.khbd.interp4j.processor.s.expr;

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
                        .addPart(new TextPart("Hello ", 0))
                        .addPart(new ExpressionPart("name1", 8))
                        .addPart(new TextPart(". ", 14))
                        .addPart(new ExpressionPart("name2", 18))
                        .addPart(new TextPart(". How are you?", 24))
        );
    }

    @Test
    public void parse_thereIsNoExpressions_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("Hello world!!!");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new TextPart("Hello world!!!", 0))
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
                        .addPart(new ExpressionPart("name", 2))
        );
    }

    @Test
    public void parse_severalExpressionWithoutTextBetween_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("${name}${age}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new ExpressionPart("name", 3))
                        .addPart(new ExpressionPart("age", 10))
        );
    }

    @Test
    public void parse_expressionContainsBracketInside_returnValidExpression() {
        Optional<SExpression> sExpression = parser.parse(" ${{{name} ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(new TextPart(" ", 0))
                        .addPart(new ExpressionPart("{{name", 3))
                        .addPart(new TextPart(" ", 10))
        );
    }

    @Test
    public void parse_thereIsNoCloseBracket_failParsing() {
        Optional<SExpression> result = parser.parse("${name");

        assertThat(result).isEmpty();
    }

    @Test
    public void parse_thereIsNoBrackets_failParsing() {
        Optional<SExpression> result = parser.parse("$name");

        assertThat(result).isEmpty();
    }

    @Test
    public void parse_thereIsNoOpenBracket_failParsing() {
        Optional<SExpression> result = parser.parse("$}} name");

        assertThat(result).isEmpty();
    }
}
