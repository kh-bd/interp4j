package dev.khbd.interp4j.processor.s;

import com.github.javaparser.Range;
import lombok.Value;

/**
 * @author Sergei_Khadanovich
 */
@Value
public class InterpolationProblem {
    Range range;
    String message;
    ProblemKind kind;

    public boolean isError() {
        return kind == ProblemKind.ERROR;
    }

    public boolean isWarn() {
        return kind == ProblemKind.WARN;
    }
}
