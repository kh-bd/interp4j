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
        CompilationResult result = compiler.compile("/cases/fmt/only_text/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.only_text.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test
    public void interpolate_nSpecifierAtTheEnd_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/fmt/n_at_the_end/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.n_at_the_end.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex\n");
    }

    @Test
    public void interpolate_nSpecifierBeforeText_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/fmt/n_before_text/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.n_before_text.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex.\nAnd what is yours?");
    }

    @Test
    public void interpolate_percentSpecifierAtTheEnd_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/fmt/percent_at_the_end/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.percent_at_the_end.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex%");
    }

    @Test
    public void interpolate_percentSpecifierBeforeText_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/fmt/percent_before_text/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.percent_before_text.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("55%. Okey?");
    }

    @Test
    public void interpolate_validExpressionWithCodes_interpolate() throws Exception {
        CompilationResult result = compiler.compile("/cases/fmt/valid_with_codes/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.fmt.valid_with_codes.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex. Are you 20?");
    }
}
