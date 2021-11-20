package dev.khbd.interp4j.processor.s;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolationResult {

    private final List<InterpolationProblem> problems;
    @Getter
    private final boolean fail;

    public InterpolationResult(List<InterpolationProblem> problems) {
        this.problems = new ArrayList<>(problems);
        this.fail = problems.stream().anyMatch(InterpolationProblem::isError);
    }

    public boolean isSuccess() {
        return !isFail();
    }

    public List<InterpolationProblem> getProblems() {
        return new ArrayList<>(problems);
    }
}
