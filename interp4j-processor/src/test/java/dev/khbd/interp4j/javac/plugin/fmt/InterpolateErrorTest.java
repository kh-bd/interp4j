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
        CompilationResult result = compiler.compile("/cases/fmt/no_closing_brace/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Wrong expression format");
    }

    @Test
    public void interpolate_expressionWithoutSpecifier_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/code_without_specifier/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed without specifier");
    }

    @Test
    public void interpolate_expressionWithoutSpecifierAtFirstPosition_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/first_code/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed without specifier");
    }

    @Test
    public void interpolate_specifierWithoutExpression_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/specifier_at_start_no_code/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Specifier is not allowed without expression");
    }

    @Test
    public void interpolate_specifierWithoutExpressionAfterText_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/specifier_with_no_code/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Specifier is not allowed without expression");
    }

    @Test
    public void interpolate_doublePercentWithExpression_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/percent_with_code/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed after %% or %n specifiers");
    }

    @Test
    public void interpolate_nSpecifierWithExpression_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/n_with_code/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Expression is not allowed after %% or %n specifiers");
    }

    @Test
    public void interpolate_explicitPosition_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/numeric_index/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Indexing is not allowed");
    }

    @Test
    public void interpolate_implicitPosition_reportError() {
        CompilationResult result = compiler.compile("/cases/fmt/implicit_index/Main.java");

        assertThat(result.isFail()).isTrue();
        assertThat(result.getErrors()).hasSize(1)
                .extracting(d -> d.getMessage(Locale.getDefault()))
                .containsExactly("Indexing is not allowed");
    }
}
