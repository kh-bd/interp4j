package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodInvocationTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sAtMethodArgumentPosition_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/method_invocation/at_arguments_position/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.method_invocation.at_arguments_position.Main");
        Method method = clazz.getMethod("greet", String.class);
        String greet = (String) method.invoke(null, "Alex");

        assertThat(greet).isEqualTo("It's Alex. Alex is 20");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sAtReceiverPosition_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/method_invocation/at_receiver_position/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.method_invocation.at_receiver_position.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("HI, ALEX");
    }
}
