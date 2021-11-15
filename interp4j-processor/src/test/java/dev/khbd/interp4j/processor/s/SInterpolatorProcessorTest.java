package dev.khbd.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolatorProcessorTest {

    private SInterpolatorProcessor processor;

    @BeforeMethod
    public void init() {
        processor = new SInterpolatorProcessor(new ClassLoaderTypeSolver(this.getClass().getClassLoader()));
    }

    @Test(dataProvider = "casesDataProvider")
    public void test(String caseName) {
        CompilationUnit unit = loadUnit("/cases/" + caseName + "/before.java");

        unit.accept(processor, null);

        CompilationUnit expectedUnit = loadUnit("/cases/" + caseName + "/after.java");
        assertThat(unit).isEqualTo(expectedUnit);
    }

    @DataProvider
    public static Object[][] casesDataProvider() {
        return new Object[][] {
                {"assignment_import"},
                {"assignment_import_static"},
                {"assignment_fqn_access"},
        };
    }

    private CompilationUnit loadUnit(String path) {
        return StaticJavaParser.parse(this.getClass().getResourceAsStream(path));
    }
}