package com.github.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class ExpressionParserTest {

    private final ExpressionParser parser = new ExpressionParser();

    @Test
    public void parse_thereIsOnExpression_returnResult() {
        ParseResult result = parser.parse("Hello ${name}. How are you?");

        ParseResult expectedResult =
                new ParseResult().addPart("Hello ")
                        .addExpression("name").addPart(". How are you?");
        assertThat(result).isEqualTo(expectedResult);
    }
}