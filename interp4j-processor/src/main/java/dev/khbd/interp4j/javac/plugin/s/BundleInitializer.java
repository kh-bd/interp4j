package dev.khbd.interp4j.javac.plugin.s;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JavacMessages;
import lombok.experimental.UtilityClass;

import java.util.ResourceBundle;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class BundleInitializer {

    private static final String BUNDLE_NAME = "dev.khbd.interp4j.javac.plugin.s.i18n.Interp4jBundle";

    public static void initPluginBundle(Context context) {
        JavacMessages messages = JavacMessages.instance(context);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        messages.add(l -> bundle);
    }
}
