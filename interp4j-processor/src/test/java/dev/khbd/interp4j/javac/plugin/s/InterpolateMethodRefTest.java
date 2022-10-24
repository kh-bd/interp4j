package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateMethodRefTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInMethodRef_interpolate(PluginOptions options) throws Exception {
        CompilationResult result = compiler.compile(options, "/cases/member_ref/Main.java");

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.member_ref.Main");
        Method method = clazz.getMethod("greet", String.class);
        String greet = (String) method.invoke(null, "Alex");

        assertThat(greet).isEqualTo("HELLO, ALEX");
    }
}
