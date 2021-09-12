package dev.khbd.interp4j.core.internal.s;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolatorTest {

    @Test
    public void interpolate_withoutExpression_returnOriginalString() {
        SInterpolator interpolator = new SInterpolator("part1");

        String result = interpolator.interpolate();

        assertThat(result).isEqualTo("part1");
    }

    @Test
    public void interpolate_withOneExpression_returnComposedString() {
        SInterpolator interpolator = new SInterpolator("part1", "part2");

        String result = interpolator.interpolate("|expr1|");

        assertThat(result).isEqualTo("part1|expr1|part2");
    }

    @Test
    public void interpolate_withTwoExpressions_returnComposedString() {
        SInterpolator interpolator = new SInterpolator("part1", "part2", "part3");

        String result = interpolator.interpolate("|expr1|", "|expr2|");

        assertThat(result).isEqualTo("part1|expr1|part2|expr2|part3");
    }
}