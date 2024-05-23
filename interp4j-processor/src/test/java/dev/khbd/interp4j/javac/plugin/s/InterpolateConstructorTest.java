package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateConstructorTest extends AbstractPluginTest {

    @Test
    public void interpolate_inConstructorArguments_interpolate() throws Exception {
        String source = """
                package cases.constructor_call;
                
                import static dev.khbd.interp4j.core.Interpolations.s;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return new Greeter(s("Hello, $name")).greet();
                    }
                
                    private static class Greeter {
                        final String name;
                
                        Greeter(String name) {
                            this.name = name;
                        }
                
                        String greet() {
                            return name;
                        }
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/constructor_call/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.constructor_call.Main");
        Method method = clazz.getMethod("greet");

        String greet = (String) method.invoke(null);
        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
