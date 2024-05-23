package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateConditionalExpressionTest extends AbstractPluginTest {

    private static final String SOURCE = """
            package cases.conditional_expression;
            
            import static dev.khbd.interp4j.core.Interpolations.s;
            
            public class Main {
            
                 public static String greet(boolean flag) {
                      String name = "Alex";
                      return flag ? s("Hello, ${name}") : s("Hi, ${name}");
                 }
            }
            """;

    @Test
    public void interpolate_sInTrueCondition_interpolate() throws Exception {
        CompilationResult result = compiler.compile("cases/conditional_expression/Main.java", SOURCE);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.conditional_expression.Main");
        Method method = clazz.getMethod("greet", boolean.class);
        String greet = (String) method.invoke(null, true);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test
    public void interpolate_sInFalseCondition_interpolate() throws Exception {
        CompilationResult result = compiler.compile("cases/conditional_expression/Main.java", SOURCE);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.conditional_expression.Main");
        Method method = clazz.getMethod("greet", boolean.class);
        String greet = (String) method.invoke(null, false);

        assertThat(greet).isEqualTo("Hi, Alex");
    }
}
