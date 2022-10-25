package dev.khbd.interp4j.javac.plugin.s;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateSwitchTest extends AbstractPluginTest {

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInReceiverExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.in_switch.receiver;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet(boolean isBos, String name) {
                        String result;
                        // really weird case to support s function here, but maybe someone will need it
                        switch (s("${chooseGreet(isBos)}, $name")) {
                            case "Hello, Alex":
                                result = "Alex was greeted correctly";
                                break;
                            case "Hi, Alex":
                                result = "Alex was greeted incorrectly";
                                break;
                            default:
                                throw new RuntimeException("Error");
                        }
                        return result;
                    }
                                
                    private static String chooseGreet(boolean isBos) {
                        return isBos ? "Hello" : "Hi";
                    }
                                
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/in_switch/receiver/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.receiver.Main");
        Method method = clazz.getMethod("greet", boolean.class, String.class);

        String greet = (String) method.invoke(null, true, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted correctly");
        greet = (String) method.invoke(null, false, "Alex");
        assertThat(greet).isEqualTo("Alex was greeted incorrectly");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseExpression_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.in_switch.expression;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet(String name) {
                        String result = switch (name) {
                            case "Alex" -> s("Hello, $name");
                            default -> "Who are you?";
                        };
                        return result;
                    }
                                
                }
                """;

        CompilationResult result = compiler.compile(options, "/cases/in_switch/expression/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.expression.Main");
        Method method = clazz.getMethod("greet", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseStatementWithYield_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.in_switch.statement;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                                
                public class Main {
                                
                    public static String greet(String name) {
                        String result = switch (name) {
                            case "Alex" -> {
                                System.out.println("Some statements...");
                                yield s("Hello, $name");
                            }
                            default     -> "Who are you?";
                        };
                        return result;
                    }
                                
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/in_switch/statement/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.statement.Main");
        Method method = clazz.getMethod("greet", String.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Hello, Alex");
        greet = (String) method.invoke(null, "Sergei");
        assertThat(greet).isEqualTo("Who are you?");
    }

    @Test(dataProvider = "optionsDataProvider")
    public void interpolate_sInCaseGuard_interpolate(PluginOptions options) throws Exception {
        String source = """
                package cases.in_switch.guard;
                                
                import static dev.khbd.interp4j.core.Interpolations.s;
                               
                public class Main {
                                
                    public static String greet(Object name) {
                        String result = switch (name) {
                            case String str && s("_${str.toUpperCase()}_").length() > 4 -> str;
                            default -> "Opps!";
                        };
                        return result;
                    }
                                
                }
                """;

        CompilationResult result = compiler.compile(options, "cases/in_switch/guard/Main.java", source);

        assertThat(result.isSuccess()).isTrue();

        ClassLoader classLoader = result.getClassLoader();
        Class<?> clazz = classLoader.loadClass("cases.in_switch.guard.Main");
        Method method = clazz.getMethod("greet", Object.class);

        String greet = (String) method.invoke(null, "Alex");
        assertThat(greet).isEqualTo("Alex");
        greet = (String) method.invoke(null, "Mi");
        assertThat(greet).isEqualTo("Opps!");
    }
}
