package dev.khbd.interp4j.processor.s;

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
            SExpression expr = (SExpression) seq.get(0);
            if (seq.size() > 1) {
                List<Object> other = (List<Object>) seq.get(1);
                for (Object o : other) {
                    expr = expr.merge((SExpression) o);
                }
            }
            return expr;
        });

        def(EXPRESSION_AND_TEXT, ref(EXPRESSION).seq(ref(TEXT)));
        action(EXPRESSION_AND_TEXT, (List<Object> seq) -> ((SExpression) seq.get(0)).merge(((SExpression) seq.get(1))));

        def(EXPRESSION, expressionParser());
        action(EXPRESSION, (String expr) -> new SExpression().addExpression(expr));

        def(TEXT, textParser());
        action(TEXT, (String text) -> new SExpression().addPart(text));
    }

    private static Parser expressionParser() {
        Parser openParser = StringParser.of("${");
        Parser expressionBodyParser = CharacterParser.noneOf("}").star();
        Parser closeParser = CharacterParser.of('}');
        return openParser.seq(expressionBodyParser).seq(closeParser)
                .flatten()
                .map((String expr) -> expr.substring(2, expr.length() - 1));
    }

    private static Parser textParser() {
        return CharacterParser.noneOf("$").star().flatten();
    }
}
