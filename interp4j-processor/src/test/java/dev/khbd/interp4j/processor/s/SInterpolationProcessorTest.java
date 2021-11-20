package dev.khbd.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationProcessorTest {

    @Mock
    private Reporter reporter;

    private AutoCloseable closeMockHandle;

    @Test(dataProvider = "validCasesDataProvider")
    public void process_interpolatorUsageDetected_replaceIt(String caseName) {
        CompilationUnit unit = loadUnit("/cases/" + caseName + "/before.java");

        SInterpolationProcessor.getInstance().process(unit, reporter);

        verify(reporter, never()).reportError(any(), any());
        CompilationUnit expectedUnit = loadUnit("/cases/" + caseName + "/after.java");
        assertThat(unit).isEqualTo(expectedUnit);
    }

    @Test
    public void process_interpolatorUsedWithNonLiteralString_reportError() {
        CompilationUnit unit = loadUnit("/cases/non_literal_string_used/before.java");

        SInterpolationProcessor.getInstance().process(unit, reporter);

        CompilationUnit expectedUnit = loadUnit("/cases/non_literal_string_used/before.java");
        assertThat(unit).isEqualTo(expectedUnit); // unchanged
        verify(reporter, times(1))
                .reportError(any(), eq("Only string literal value is supported"));
    }

    @Test
    public void process_interpolatorUsedWithNullLiteralValue_reportError() {
        CompilationUnit unit = loadUnit("/cases/null_literal_value_used/before.java");

        SInterpolationProcessor.getInstance().process(unit, reporter);

        CompilationUnit expectedUnit = loadUnit("/cases/null_literal_value_used/before.java");
        assertThat(unit).isEqualTo(expectedUnit); // unchanged
        verify(reporter, times(1))
                .reportError(any(), eq("Only string literal value is supported"));
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

    @BeforeMethod
    public void before() {
        closeMockHandle = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void after() throws Exception {
        closeMockHandle.close();
    }
}