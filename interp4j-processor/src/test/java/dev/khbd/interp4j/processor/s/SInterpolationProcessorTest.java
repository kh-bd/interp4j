package dev.khbd.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationProcessorTest {

    @Test(dataProvider = "validCasesDataProvider")
    public void process_interpolatorUsageDetected_replaceIt(String caseName) {
        CompilationUnit unit = loadUnit("/cases/" + caseName + "/before.java");

        ProcessingResult result = SInterpolationProcessor.getInstance().process(unit);

        CompilationUnit expectedUnit = loadUnit("/cases/" + caseName + "/after.java");
        assertThat(unit).isEqualTo(expectedUnit);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void process_interpolatorUsedWithNonLiteralString_reportError() {
        CompilationUnit unit = loadUnit("/cases/non_literal_string_used/before.java");

        ProcessingResult result = SInterpolationProcessor.getInstance().process(unit);

        CompilationUnit expectedUnit = loadUnit("/cases/non_literal_string_used/before.java");
        assertThat(unit).isEqualTo(expectedUnit); // unchanged
        assertThat(result.isFail()).isTrue();
        assertThat(result.getProblems())
                .extracting(ProcessingProblem::getMessage)
                .containsOnly("Only string literal value is supported");
    }

    @Test
    public void process_interpolatorUsedWithNullLiteralValue_reportError() {
        CompilationUnit unit = loadUnit("/cases/null_literal_value_used/before.java");

        ProcessingResult result = SInterpolationProcessor.getInstance().process(unit);

        CompilationUnit expectedUnit = loadUnit("/cases/null_literal_value_used/before.java");
        assertThat(unit).isEqualTo(expectedUnit); // unchanged
        assertThat(result.getProblems())
                .extracting(ProcessingProblem::getMessage)
                .containsOnly("Only string literal value is supported");
    }

    @DataProvider
    public static Object[][] validCasesDataProvider() {
        return new Object[][]{
                {"assignment_import"},
                {"assignment_import_static"},
                {"assignment_fqn_access"},
                {"without_expressions_inside"},
                {"method_arguments"},
                {"static_field_used"},
                {"complex_expression_used"},
                {"method_inside_expr"}
        };
    }

    private CompilationUnit loadUnit(String path) {
        return StaticJavaParser.parse(this.getClass().getResourceAsStream(path));
    }

    private AutoCloseable closeMockHandle;

    @BeforeMethod
    public void before() {
        closeMockHandle = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void after() throws Exception {
        closeMockHandle.close();
    }
}