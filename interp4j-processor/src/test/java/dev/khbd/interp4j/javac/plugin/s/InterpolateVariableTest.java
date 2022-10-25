package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateVariableTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_localVariableDeclarationWithExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.local_variable.declaration;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        String name = "Alex";
                        String greet = s("Hello, ${name}");
                        return greet;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/local_variable/declaration/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.local_variable.declaration.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_localVariableAssignmentWithExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.local_variable.assignment;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        String name = "Alex";
                        String greet = null;
                        greet = s("Hello, ${name}");
                        return greet;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/local_variable/assignment/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.local_variable.assignment.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_localVariableCompoundAssignmentWithExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.local_variable.compound_assignment;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet() {
                        String name = "Alex";
                        String greet = "H";
                        greet += s("ello, ${name}");
                        return greet;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/local_variable/compound_assignment/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.local_variable.compound_assignment.Main");
        Method method = clazz.getMethod("greet");
        String greet = (String) method.invoke(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_staticVariableAssignmentWithExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.static_variable.assignment;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static final String GREET;
                                
                    static {
                        String name = "Alex";
                        GREET = s("Hello, $name");
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/static_variable/assignment/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.static_variable.assignment.Main");
        Field field = clazz.getField("GREET");
        String greet = (String) field.get(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_staticVariableDeclarationWithExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.static_variable.declaration;
                               
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    private static String NAME = "Alex";
                    public static final String GREET = s("Hello, $NAME");
                                
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/static_variable/declaration/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.static_variable.declaration.Main");
        Field field = clazz.getField("GREET");
        String greet = (String) field.get(null);

        assertThat(greet).isEqualTo("Hello, Alex");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_nonLiteralUsed_reportError(PluginOptions options) {
        String source = """
                package cases.local_variable.non_literal_string_used;
                                
                import dev.khbd.interp4j.core.Interpolations;
                                
                public class Main {
                                
                    private static final String EXPR = "Hello ${name}";
                                
                    public static String greet() {
                        String greet = Interpolations.s(EXPR);
                        return greet;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/local_variable/non_literal_string_used/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Only string literal is supported here");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_expressionIsWrong_reportError(PluginOptions options) {
        String source = """
                package cases.local_variable.wrong_expression;
                                
                import dev.khbd.interp4j.core.Interpolations;
                                
                public class Main {
                                
                    public static String greet() {
                        String name = "Alex";
                        String greet = Interpolations.s("Hello ${Alex");
                        return greet;
                    }
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/local_variable/wrong_expression/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Wrong expression format");
    }
}
