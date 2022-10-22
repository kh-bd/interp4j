package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSwitchTest extends AbstractPluginTest {

    @Test
    public void interpolate_sInReceiverExpression_interpolate() throws Exception {
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

    @Test
    public void interpolate_sInCaseExpression_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/in_switch/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.Main");
        Method method = clazz.getMethod("greetExpression", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }

    @Test
    public void interpolate_sInCaseStatementWithYield_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/in_switch/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.Main");
        Method method = clazz.getMethod("greetYield", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }
}
