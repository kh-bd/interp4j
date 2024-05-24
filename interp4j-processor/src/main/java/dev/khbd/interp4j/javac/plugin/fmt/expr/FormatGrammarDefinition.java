package dev.khbd.interp4j.javac.plugin.fmt.expr;

import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.GrammarDefinition;

/**
 * Grammar definition for 'fmt' expression parser.
 *
 * @author Sergei Khadanovich
 */
class FormatGrammarDefinition extends GrammarDefinition {

    FormatGrammarDefinition() {
        def("start", StringParser.of(""));
    }
}
