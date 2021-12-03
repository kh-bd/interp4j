package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateBinaryOperatorTest extends AbstractPluginTest {

    @Test
    public void interpolate_inBinaryOperator_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/in_binary_operator/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_binary_operator.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }
}
