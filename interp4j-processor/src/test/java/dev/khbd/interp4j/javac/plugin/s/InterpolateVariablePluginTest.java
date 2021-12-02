package dev.khbd.interp4j.javac.plugin.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateVariablePluginTest extends AbstractPluginTest {

    @Test
    public void interpolate_localVariableDeclarationWithExpression_interpolate() throws Exception {
        ClassLoader classLoader = compiler.compile("/cases/local_variable/declaration/Main.java");
        Class<?> clazz = classLoader.loadClass("cases.local_variable.declaration.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test
    public void interpolate_localVariableAssignmentWithExpression_interpolate() throws Exception {
        ClassLoader classLoader = compiler.compile("/cases/local_variable/assignment/Main.java");
        Class<?> clazz = classLoader.loadClass("cases.local_variable.assignment.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }
}
