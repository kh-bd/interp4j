package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateExpressionInParenthesesTest extends AbstractPluginTest {

    @Test
    public void interpolate_sInReturnStatement_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/in_parentheses/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_parentheses.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
