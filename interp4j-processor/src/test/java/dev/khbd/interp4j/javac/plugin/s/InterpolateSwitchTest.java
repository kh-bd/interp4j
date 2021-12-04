package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSwitchTest extends AbstractPluginTest {

    @Test
    public void interpolate_sInExpression_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/in_switch/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.Main");
        Method method = clazz.getMethod("greet", boolean.class, String.class);

        String greet = (String) method.invoke(null, true, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted correctly");
        greet = (String) method.invoke(null, false, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted incorrectly");
    }
}
