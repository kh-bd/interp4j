package dev.khbd.interp4j.javac.plugin.i18n;

import java.util.ListResourceBundle;

/**
 * @author Sergei_Khadanovich
 */
public class Interp4jBundle extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"compiler.err.non.string.literal", "Only string literal is supported here"},
                {"compiler.err.wrong.expression.format", "Wrong expression format"},

                {"compiler.err.fmt.expression.without.specifier", "Expression is not allowed without specifier"},
                {"compiler.err.fmt.specifier.without.expression", "Specifier is not allowed without expression"},
                {"compiler.err.fmt.expression.after.special.specifiers", "Expression is not allowed after %% or %n specifiers"},
                {"compiler.err.fmt.indexing", "Indexing is not allowed"},
        };
    }
}
