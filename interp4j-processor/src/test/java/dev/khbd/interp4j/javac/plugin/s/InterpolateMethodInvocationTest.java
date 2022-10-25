package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodInvocationTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sAtMethodArgumentPosition_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.method_invocation.at_arguments_position;
                               
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    private static final int AGE = 20;
                                
                    public static String greet(String name) {
                        return concat(s("It's ${name}. "), s("$name is $AGE"));
                    }
                                
                    private static String concat(String str1, String str2) {
                        return str1 + str2;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/method_invocation/at_arguments_position/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.method_invocation.at_arguments_position.Main");
        Method method = clazz.getMethod("greet", String.class);
        String greet = (String) method.invoke(null, "Alex");

        assertThat(greet).isEqualTo("It's Alex. Alex is 20");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sAtReceiverPosition_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.method_invocation.at_receiver_position;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        String name = "Alex";
                        return s("  Hello, $name  ").trim().replace("Hello", "Hi").toUpperCase();
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/method_invocation/at_receiver_position/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.method_invocation.at_receiver_position.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("HI, ALEX");
    }
}
