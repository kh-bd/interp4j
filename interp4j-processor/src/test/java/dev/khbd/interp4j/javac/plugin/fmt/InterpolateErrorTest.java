package dev.khbd.interp4j.javac.plugin.fmt;

import static org.assertj.core.api.Assertions.assertThat;

import dev.khbd.interp4j.javac.plugin.AbstractPluginTest;
import org.testng.annotations.Test;

import java.util.Locale;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolateErrorTest extends AbstractPluginTest {

    @Test
    public void interpolate_expressionIsWrong_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %s${name");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Wrong expression format");
    }

    @Test
    public void interpolate_expressionWithoutSpecifier_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello ${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed without specifier");
    }

    @Test
    public void interpolate_expressionWithoutSpecifierAtFirstPosition_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed without specifier");
    }

    @Test
    public void interpolate_specifierWithoutExpression_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("%s");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Specifier is not allowed without expression");
    }

    @Test
    public void interpolate_specifierWithoutExpressionAfterText_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %s");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Specifier is not allowed without expression");
    }

    @Test
    public void interpolate_doublePercentWithExpression_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %%${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed after %% or %n specifiers");
    }

    @Test
    public void interpolate_nSpecifierWithExpression_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %n${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed after %% or %n specifiers");
    }

    @Test
    public void interpolate_explicitPosition_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %1$s${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Indexing is not allowed");
    }

    @Test
    public void interpolate_implicitPosition_reportError() {
        String source = """
                package cases.wrong_format;
                
                import static dev.khbd.interp4j.core.Interpolations.*;
                
                public class Main {
                
                    public static String greet() {
                        String name = "Alex";
                        return fmt("Hello %<s${name}");
                    }
                }
                """;

        CompilationResult result = compiler.compile("cases/wrong_format/Main.java", source);

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Indexing is not allowed");
    }
}
