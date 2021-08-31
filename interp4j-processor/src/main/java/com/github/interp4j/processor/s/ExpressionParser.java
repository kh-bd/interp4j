package com.github.interp4j.processor.s;

/**
 * @author Sergei_Khadanovich
 */
class ExpressionParser {

    /**
     * Parse string literal.
     *
     * @param literal string literal
     * @return parse result
     */
    ParseResult parse(String literal) {
        int start = literal.indexOf("${");
        int end = literal.indexOf("}");

        ParseResult result = new ParseResult();
        result.addPart(literal.substring(0, start));
        result.addPart(literal.substring(end + 1));
        result.addExpression(literal.substring(start + 2, end));

        return result;
    }
}
