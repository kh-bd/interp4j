package dev.khbd.interp4j.processor.s.expr;

import lombok.Value;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.List;

/**
 * 'S' expression grammar.
 *
 * @author Sergei_Khadanovich
 */
class SGrammarDefinition extends GrammarDefinition {

    private static final String TEXT = "text";
    private static final String EXPRESSION = "expression";
    private static final String EXPRESSION_AND_TEXT = "expressionAndText";

    SGrammarDefinition() {
        def("start",
                ref(TEXT)
                        .seq(ref(EXPRESSION_AND_TEXT).star())
                        .end()
        );
        action("start", (List<Object> seq) -> {
            SExpression expression = new SExpression();
            addNotEmptyTextPart(expression, (TextPart) seq.get(0));
            if (seq.size() > 1) {
                List<ExpressionAndText> other = (List<ExpressionAndText>) seq.get(1);
                for (ExpressionAndText exprAndText : other) {
                    expression.addPart(exprAndText.getExpression());
                    addNotEmptyTextPart(expression, exprAndText.getText());
                }
            }
            return expression;
        });

        def(EXPRESSION_AND_TEXT, ref(EXPRESSION).seq(ref(TEXT)));
        action(EXPRESSION_AND_TEXT, (List<Object> seq) ->
                new ExpressionAndText((ExpressionPart) seq.get(0), (TextPart) seq.get(1)));

        def(EXPRESSION, expressionParser());
        action(EXPRESSION, (Token token) -> new ExpressionPart(token.getValue(), token.getStart()));

        def(TEXT, textParser());
        action(TEXT, (Token token) -> new TextPart(token.getValue(), token.getStart()));
    }

    private void addNotEmptyTextPart(SExpression expression, TextPart text) {
        if (text.getText().isEmpty()) {
            return;
        }
        expression.addPart(text);
    }

    private static Parser expressionParser() {
        Parser openParser = StringParser.of("${");
        Parser expressionBodyParser = CharacterParser.noneOf("}").star().flatten().token();
        Parser closeParser = CharacterParser.of('}');
        return openParser.seq(expressionBodyParser).seq(closeParser)
                .map((List<Object> seq) -> seq.get(1));
    }

    private static Parser textParser() {
        return StringParser.of("$$")
                .or(CharacterParser.noneOf("$"))
                .star()
                .flatten()
                .token();
    }

    @Value
    private static class ExpressionAndText {
        ExpressionPart expression;
        TextPart text;
    }
}
