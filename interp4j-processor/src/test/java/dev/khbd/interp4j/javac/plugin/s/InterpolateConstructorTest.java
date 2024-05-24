package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateConstructorTest extends AbstractPluginTest {

    @Test
    public void interpolate_inConstructorArguments_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/constructor_call/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.constructor_call.Main");
        Method method = clazz.getMethod("greet");

        String greet = (String) method.invoke(null);
        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
