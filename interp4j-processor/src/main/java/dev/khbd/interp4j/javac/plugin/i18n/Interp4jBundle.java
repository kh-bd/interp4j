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
                {"compiler.err.wrong.expression.format", "Wrong expression format"}
        };
    }
}
