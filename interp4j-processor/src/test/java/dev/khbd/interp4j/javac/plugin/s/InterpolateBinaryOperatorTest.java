package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateBinaryOperatorTest extends AbstractPluginTest {

    @Test
    public void interpolate_inBinaryOperator_interpolate() throws Exception {
        String source = """
                package cases.in_binary_operator;
                
                import static dev.khbd.interp4j.core.Interpolations.s;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return s("I'm ${name}. ") + s("I'm ${name.toUpperCase()}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/in_binary_operator/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_binary_operator.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }
}
