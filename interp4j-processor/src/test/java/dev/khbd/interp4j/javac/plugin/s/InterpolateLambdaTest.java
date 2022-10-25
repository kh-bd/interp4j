package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateLambdaTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_interpolationAtReturnStatementPositionInLambda_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.lambda.statement;
                                
                import java.util.function.Function;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        Function<String, String> f = (name) -> {
                            return s("Hello, $name");
                        };
                        return f.apply("Alex");
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/lambda/statement/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.lambda.statement.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_interpolationAtReturnExpressionPositionInLambda_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.lambda.expression;
                               
                import java.util.function.Function;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        Function<String, String> f = (name) -> s("Hello, $name");
                        return f.apply("Alex");
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/lambda/expression/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.lambda.expression.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
