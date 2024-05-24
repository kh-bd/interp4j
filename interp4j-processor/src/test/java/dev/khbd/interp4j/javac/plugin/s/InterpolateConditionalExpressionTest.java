package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateConditionalExpressionTest extends AbstractPluginTest {

    @Test
    public void interpolate_sInTrueCondition_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/conditional_expression/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.conditional_expression.Main");
        Method method = clazz.getMethod("greet", boolean.class);
        String greet = (String) method.invoke(null, true);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test
    public void interpolate_sInFalseCondition_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/conditional_expression/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.conditional_expression.Main");
        Method method = clazz.getMethod("greet", boolean.class);
        String greet = (String) method.invoke(null, false);

        assertThat(greet).isEqualTo("Hi, Alex");
    }
}
