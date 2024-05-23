package dev.khbd.interp4j.javac.plugin.fmt;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSuccessTest extends AbstractPluginTest {

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
    public void interpolate_nSpecifierAtTheEnd_interpolate() throws Exception {
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

    @Test
    public void interpolate_nSpecifierBeforeText_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        return fmt("Hello, Alex.%nAnd what is yours?");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex.\nAnd what is yours?");
    }

    @Test
    public void interpolate_percentSpecifierAtTheEnd_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        return fmt("Hello, Alex%%");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex%");
    }

    @Test
    public void interpolate_percentSpecifierBeforeText_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        return fmt("55%%. Okey?");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("55%. Okey?");
    }

    @Test
    public void interpolate_validExpressionWithCodes_interpolate() throws Exception {
        String source = """
                package cases.simple;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        int age = 20;
                        return fmt("Hello, %s${name}. Are you %d${age}?");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/simple/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.simple.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex. Are you 20?");
    }
}
