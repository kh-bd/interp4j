package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class WildcardImportTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_wildcardMethodsImport_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/wildcard/methods/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard.methods.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_wildcardPackageImport_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "cases/wildcard/classes/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard.classes.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }
}
