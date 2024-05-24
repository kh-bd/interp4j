package dev.khbd.interp4j.javac.plugin.fmt;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.CharacterParser.digit;

import lombok.Value;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Grammar definition for 'fmt' expression parser.
 *
 * @author Sergei Khadanovich
 */
class FormatGrammarDefinition extends GrammarDefinition {

    private static final String TEXT = "text";
    private static final String CODE = "code";
    private static final String SPECIFIER = "specifier";
    private static final String INDEX = "index";
    private static final String NUMERIC_INDEX = "numericIndex";
    private static final String IMPLICIT_INDEX = "implicitIndex";
    private static final String FLAGS = "flags";
    private static final String WIDTH = "width";
    private static final String PRECISION = "precision";
    private static final String CONVERSION = "conversion";
    private static final String CODE_TEXT = "codeAndText";
    private static final String SPECIFIER_TEXT = "specifierAndText";

    FormatGrammarDefinition() {
        def("start",
                ref(TEXT)
                        .seq(ref(SPECIFIER_TEXT).or(ref(CODE_TEXT)).star())
                        .end()
        );
        action("start", (List<Object> seq) -> {
            FormatExpressionBuilder builder = FormatExpression.builder();

            FormatText text = (FormatText) seq.get(0);
            if (!text.isEmpty()) {
                builder.text(text);
            }

            if (seq.size() > 1) {
                List<AndText<?>> other = (List<AndText<?>>) seq.get(1);
                for (AndText<?> andText : other) {
                    FormatExpressionPart part = andText.value;

                    if (part.isCode()) {
                        builder.code((FormatCode) part);
                    } else if (part.isSpecifier()) {
                        builder.specifier((FormatSpecifier) part);
                    } else {
                        throw new IllegalStateException("Only specifier and code are supported");
                    }

                    if (!andText.text.isEmpty()) {
                        builder.text(andText.text);
                    }
                }
            }

            return builder.build();
        });

        def(SPECIFIER_TEXT, ref(SPECIFIER).seq(ref(TEXT)));
        action(SPECIFIER_TEXT, (List<Object> seq) ->
                new AndText<>((FormatSpecifier) seq.get(0), (FormatText) seq.get(1)));

        def(CODE_TEXT, ref(CODE).seq(ref(TEXT)));
        action(CODE_TEXT, (List<Object> seq) ->
                new AndText<>((FormatCode) seq.get(0), (FormatText) seq.get(1)));

        def(TEXT, textParser());
        action(TEXT, (Token token) -> new FormatText(token.getValue(), new Position(token.getStart(), token.getStop())));

        // code
        def(CODE, codeWithBrackets().or(codeWithoutBrackets()));
        action(CODE, (Token token) -> new FormatCode(token.getValue(), new Position(token.getStart(), token.getStop())));

        // specifier
        def(SPECIFIER,
                character('%')
                        .seq(ref(INDEX).optional())
                        .seq(ref(FLAGS).optional())
                        .seq(ref(WIDTH).optional())
                        .seq(ref(PRECISION).optional())
                        .seq(ref(CONVERSION))
                        .token()
        );
        action(SPECIFIER, (Token token) -> {
            List<Object> args = token.getValue();
            return new FormatSpecifier(getTyped(args, 1), getTyped(args, 2),
                    getTyped(args, 3), getTyped(args, 4),
                    getTyped(args, 5), new Position(token.getStart(), token.getStop())
            );
        });

        // index
        def(INDEX, ref(NUMERIC_INDEX).or(ref(IMPLICIT_INDEX)));
        def(NUMERIC_INDEX, numericIndex());
        def(IMPLICIT_INDEX, implicitIndex());

        // flags
        def(FLAGS, flags());

        // conversion
        def(CONVERSION, conversion());

        // width
        def(WIDTH, number());

        // precision
        def(PRECISION, character('.').seq(number()).pick(1));
    }

    private static Parser textParser() {
        return StringParser.of("$$").map(seq -> "$")
                .or(CharacterParser.noneOf("$%").flatten())
                .star()
                .map(seq -> String.join("", (List<String>) seq))
                .token();
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

    private static Parser conversion() {
        Parser dateTimeConversionSuffix = dateTimeConversionSuffix();
        return anyOf("bB")
                .or(anyOf("hH"))
                .or(anyOf("sS"))
                .or(anyOf("cC"))
                .or(character('d'))
                .or(character('o'))
                .or(anyOf("xX"))
                .or(anyOf("eE"))
                .or(character('f'))
                .or(anyOf("gG"))
                .or(anyOf("aA"))
                .or(character('t').seq(dateTimeConversionSuffix).map(listAsString()))
                .or(character('T').seq(dateTimeConversionSuffix).map(listAsString()))
                .or(character('%'))
                .or(character('n'))
                .map(asString()).map(conv -> new Conversion((String) conv));
    }

    private static Parser numericIndex() {
        return digit().map(asString())
                .seq(digit().star().flatten())
                .seq(character('$'))
                .map((List<Object> args) -> {
                    String firstDigit = getTyped(args, 0);
                    int position = Integer.parseInt(firstDigit + getTyped(args, 1));
                    return new NumericIndex(position);
                });
    }

    private static Parser implicitIndex() {
        return character('<').map(it -> ImplicitIndex.INSTANCE);
    }

    private static Parser number() {
        return digit().map(asString())
                .seq(digit().star().flatten())
                .map((List<Object> args) -> {
                    String firstDigit = getTyped(args, 0);
                    return Integer.parseInt(firstDigit + getTyped(args, 1));
                });
    }

    private static Parser flags() {
        return anyOf("-#+ 0,(").plus().map(listAsString());
    }

    private static Parser dateTimeConversionSuffix() {
        return anyOf("HIklMSLNPzZsQBbHAaCYyjmdeRTrDFc");
    }

    private static Parser character(char c) {
        return CharacterParser.of(c);
    }

    private static <T> Function<T, String> asString() {
        return Objects::toString;
    }

    private static Function<List<Object>, String> listAsString() {
        return args -> args.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(""));
    }

    private static String makeString(List<Character> chs) {
        StringBuilder builder = new StringBuilder();
        for (Character ch : chs) {
            builder.append(ch);
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getTyped(List<Object> values, int index) {
        return (T) values.get(index);
    }

    @Value
    private static class AndText<T extends FormatExpressionPart> {
        T value;
        FormatText text;
    }
}
