package dev.khbd.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author Sergei_Khadanovich
 */
public class SExpressionParserTest {

    private final SExpressionParser parser = new SExpressionParser();

    @Test
    public void parse_thereIsOnExpression_returnResult() {
        Optional<SExpression> sExpression = parser.parse("Hello ${name1}. ${name2}. How are you?");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart("Hello ")
                        .addExpression("name1")
                        .addPart(". ")
                        .addExpression("name2")
                        .addPart(". How are you?")
        );
    }

    @Test
    public void parse_thereIsNoExpressions_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("Hello world!!!");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart("Hello world!!!")
        );
    }

    @Test
    public void parse_stringIsEmpty_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart("")
        );
    }

    @Test
    public void parse_expressionWithoutOtherTest_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("${name}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart("")
                        .addExpression("name")
                        .addPart("")
        );
    }

    @Test
    public void parse_severalExpressionWithoutTextBetween_returnValidSExpression() {
        Optional<SExpression> sExpression = parser.parse("${name}${age}");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart("")
                        .addExpression("name")
                        .addPart("")
                        .addExpression("age")
                        .addPart("")
        );
    }

    @Test
    public void parse_expressionContainsBracketInside_returnValidExpression() {
        Optional<SExpression> sExpression = parser.parse(" ${{{name} ");

        assertThat(sExpression).hasValue(
                new SExpression()
                        .addPart(" ")
                        .addExpression("{{name")
                        .addPart(" ")
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
