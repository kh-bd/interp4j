package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateWithEscapedSymbolsTest extends AbstractPluginTest {

    @Test
    public void interpolate_stringLiteralWithEscapedSymbols_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/escaped_symbols/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.escaped_symbols.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, \"\"Alex\"\"");
    }

}
