package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateWildcardImportTest extends AbstractPluginTest {

    @Test
    public void interpolate_wildcardMethodsImport_interpolate() throws Exception {
        String source = """
                package cases.wildcard_import;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return s("I'm ${name}. ") + s("I'm ${name.toUpperCase()}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wildcard_import/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard_import.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }

    @Test
    public void interpolate_wildcardPackageImport_interpolate() throws Exception {
        String source = """
                package cases.wildcard_import;
                
                import dev.khbd.interp4j.core.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return Interpolations.s("I'm ${name}. ") + Interpolations.s("I'm ${name.toUpperCase()}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wildcard_import/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.wildcard_import.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("I'm Alex. I'm ALEX");
    }
}
