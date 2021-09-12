package dev.khbd.interp4j.processor.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.testing.utils.ModelUtils;
import spoon.testing.utils.ProcessorUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolatorProcessorTest {

    private final SInterpolatorProcessor processor = new SInterpolatorProcessor();

    @Test(dataProvider = "successTransformationsDataProvider")
    public void successTransformationTest(String sourcePath, String expectedPath) throws URISyntaxException {
        verifySuccess(sourcePath, expectedPath);
    }

    @DataProvider
    public static Object[][] successTransformationsDataProvider() {
        return new Object[][]{
                {"/s/success/simpleVariableSource.java", "/s/success/simpleVariableExpected.java"},
                {"/s/success/staticVariableInitializerSource.java", "/s/success/staticVariableInitializerExpected.java"}
        };
    }

    @Test(dataProvider = "failTransformationsDataProvider")
    public void failTransformationTest(String sourcePath, int errorsCount) throws URISyntaxException {
        verifyFail(sourcePath, errorsCount);
    }

    @DataProvider
    public static Object[][] failTransformationsDataProvider() {
        return new Object[][]{
                {"/s/fail/nullLiteral.java", 1},
                {"/s/fail/notStringLiteral.java", 1},
                {"/s/fail/wrongFormat.java", 1}
        };
    }

    private void verifyFail(String path, int expectedErrorsCounts) throws URISyntaxException {
        Factory actualFactory = ModelUtils.build(resourceToFile(path));
        Factory expectedFactory = ModelUtils.build(resourceToFile(path));

        ProcessorUtils.process(actualFactory, List.of(processor));

        // nothing was transformed
        verifyTheSame(actualFactory, expectedFactory);

        int errorsCount = actualFactory.getEnvironment().getErrorCount();
        assertThat(errorsCount).isEqualTo(expectedErrorsCounts);
    }

    private void verifySuccess(String sourcePath, String expectedPath) throws URISyntaxException {
        Factory actualFactory = ModelUtils.build(resourceToFile(sourcePath));
        Factory expectedFactory = ModelUtils.build(resourceToFile(expectedPath));

        ProcessorUtils.process(actualFactory, List.of(processor));

        verifyTheSame(actualFactory, expectedFactory);
    }

    private void verifyTheSame(Factory actualFactory, Factory expectedFactory) {
        final List<CtType<?>> allActual = actualFactory.Type().getAll();
        final List<CtType<?>> allExpected = expectedFactory.Type().getAll();

        for (int i = 0; i < allActual.size(); i++) {
            verifyTheSame(allActual.get(i), allExpected.get(i));
        }
    }

    private void verifyTheSame(CtType<?> actual, CtType<?> expected) {
        assertThat(actual.prettyprint()).isEqualTo(expected.prettyprint());
    }

    private File resourceToFile(String path) throws URISyntaxException {
        URI uri = getClass().getResource(path).toURI();
        return Paths.get(uri).toFile();
    }
}
