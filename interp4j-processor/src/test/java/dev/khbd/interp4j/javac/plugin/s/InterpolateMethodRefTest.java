package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodRefTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInMethodRef_interpolate(PluginOptions options) throws Exception {
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

        CompilationResult result = compiler.compile(options, "cases/member_ref/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.member_ref.Main");
        Method method = clazz.getMethod("greet", String.class);
        String greet = (String) method.invoke(null, "Alex");

        assertThat(greet).isEqualTo("HELLO, ALEX");
    }
}
