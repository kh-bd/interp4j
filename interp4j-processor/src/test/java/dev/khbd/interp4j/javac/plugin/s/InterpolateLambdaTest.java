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
        CompilationResult result = compiler.compile(options, "/cases/lambda/statement/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.lambda.statement.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_interpolationAtReturnExpressionPositionInLambda_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/lambda/expression/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.lambda.expression.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
