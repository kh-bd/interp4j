package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateWildcardImportTest extends AbstractPluginTest {

    @Test
    public void interpolate_wildcardMethodsImport_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/wildcard/methods/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard.methods.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }

    @Test
    public void interpolate_wildcardPackageImport_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/wildcard/classes/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard.classes.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }
}
