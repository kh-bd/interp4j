package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodRefTest extends AbstractPluginTest {

    @Test
    public void interpolate_sInMethodRef_interpolate() throws Exception {
        String source = """
                package cases.member_ref;
                
                import static dev.khbd.interp4j.core.Interpolations.s;
                
                import java.util.function.Supplier;
                
                public class Main {
                
                    public static String greet(String name) {
                        return get(s("Hello, $name")::toUpperCase);
                    }
                
                    private static <T> T get(Supplier<? extends T> f) {
                        return f.get();
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/member_ref/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.member_ref.Main");
        Method method = clazz.getMethod("greet", String.class);
        String greet = (String) method.invoke(null, "Alex");

        assertThat(greet).isEqualTo("HELLO, ALEX");
    }
}
