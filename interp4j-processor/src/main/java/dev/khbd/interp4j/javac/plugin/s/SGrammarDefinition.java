package dev.khbd.interp4j.javac.plugin.s;

import lombok.Value;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.List;

/**
 * 'S' code grammar.
 *
 * @author Sergei_Khadanovich
 */
class SGrammarDefinition extends GrammarDefinition {

    private static final String TEXT = "text";
    private static final String CODE = "code";
    private static final String CODE_AND_TEXT = "codeAndText";

    SGrammarDefinition() {
        def("start",
                ref(TEXT)
                        .seq(ref(CODE_AND_TEXT).star())
                        .end()
        );
        action("start", (List<Object> seq) -> {
            SExpression expression = new SExpression();
            addNotEmptyTextPart(expression, (SText) seq.get(0));
            if (seq.size() > 1) {
                List<CodeAndText> other = (List<CodeAndText>) seq.get(1);
                for (CodeAndText andText : other) {
                    expression.addPart(andText.getCode());
                    addNotEmptyTextPart(expression, andText.getText());
                }
            }
            return expression;
        });

        def(CODE_AND_TEXT, ref(CODE).seq(ref(TEXT)));
        action(CODE_AND_TEXT, (List<Object> seq) ->
                new CodeAndText((SCode) seq.get(0), (SText) seq.get(1)));

        def(CODE, codeWithBrackets().or(codeWithoutBrackets()));
        action(CODE, (Token token) -> new SCode(token.getValue(), new Position(token.getStart(), token.getStop())));

        def(TEXT, textParser());
        action(TEXT, (Token token) -> new SText(token.getValue(), new Position(token.getStart(), token.getStop())));
    }

    private void addNotEmptyTextPart(SExpression expression, SText text) {
        if (text.getText().isEmpty()) {
            return;
        }
        expression.addPart(text);
    }

    private static Parser codeWithBrackets() {
        Parser openParser = StringParser.of("${");
        Parser expressionBodyParser = CharacterParser.noneOf("}").star().flatten().token();
        Parser closeParser = CharacterParser.of('}');
        return openParser.seq(expressionBodyParser).seq(closeParser)
                .map((List<Object> seq) -> seq.get(1));
    }

    private static Parser codeWithoutBrackets() {
        Parser openParser = StringParser.of("$");
        return openParser.seq(literalParser())
                .map((List<Object> seq) -> seq.get(1));
    }

    private static Parser literalParser() {
        return CharacterParser.of(Character::isJavaIdentifierStart, "")
                .map(ch -> Character.toString((char) ch))
                .seq(CharacterParser.of(Character::isJavaIdentifierPart, "").star()
                        .map(chs -> makeString((List<Character>) chs)))
                .map((List<Object> seq) -> (String) seq.get(0) + seq.get(1))
                .token();
    }

    private static String makeString(List<Character> chs) {
        StringBuilder builder = new StringBuilder();
        for (Character ch : chs) {
            builder.append(ch);
        }
        return builder.toString();
    }

    private static Parser textParser() {
        return StringParser.of("$$").map(seq -> "$")
                .or(CharacterParser.noneOf("$").flatten())
                .star()
                .map(seq -> String.join("", (List<String>) seq))
                .token();
    }

    @Value
    private static class CodeAndText {
        SCode code;
        SText text;
    }
}
