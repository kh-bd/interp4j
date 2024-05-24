package dev.khbd.interp4j.javac.plugin.fmt;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSimpleTest extends AbstractPluginTest {

    @Test
    public void interpolate_noExpressionsAndNoSpecifiersInside_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        return fmt("Hello, Alex");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test
    public void interpolate_noExpressions_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        return fmt("Hello, Alex%n");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex\n");
    }
}
