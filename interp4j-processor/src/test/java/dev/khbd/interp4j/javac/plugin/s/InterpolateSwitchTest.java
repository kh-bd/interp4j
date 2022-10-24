package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSwitchTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInReceiverExpression_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/in_switch/receiver/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.receiver.Main");
        Method method = clazz.getMethod("greet", boolean.class, String.class);

        String greet = (String) method.invoke(null, true, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted correctly");
        greet = (String) method.invoke(null, false, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted incorrectly");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseExpression_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/in_switch/expression/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.expression.Main");
        Method method = clazz.getMethod("greet", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseStatementWithYield_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/in_switch/statement/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.statement.Main");
        Method method = clazz.getMethod("greet", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseGuard_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/in_switch/guard/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.guard.Main");
        Method method = clazz.getMethod("greet", Object.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Alex");
        greet = (String) method.invoke(null, "Mi");
        assertThat(greet).isEqualTo("Opps!");
    }
}
