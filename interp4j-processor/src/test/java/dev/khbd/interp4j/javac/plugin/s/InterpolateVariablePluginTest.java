package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateVariablePluginTest extends AbstractPluginTest {

    @Test
    public void interpolate_localVariableWithExpression_interpolate() throws Exception {
        ClassLoader classLoader = compiler.compile("/cases/local_variable/Main.java");
        Class<?> clazz = classLoader.loadClass("cases.local_variable.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
