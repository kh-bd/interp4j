package dev.khbd.interp4j.processor.s;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
public class ProcessingResult {

    private final List<ProcessingProblem> problems;
    @Getter
    private final boolean fail;

    public ProcessingResult(List<ProcessingProblem> problems) {
        this.problems = new ArrayList<>(problems);
        this.fail = problems.stream().anyMatch(ProcessingProblem::isError);
    }

    /**
     * Is result success or not.
     *
     * @return {@literal true} if result is success and {@literal false} otherwise
     */
    public boolean isSuccess() {
        return !isFail();
    }

    public List<ProcessingProblem> getProblems() {
        return new ArrayList<>(problems);
    }

    public static ProcessingResultBuilder builder() {
        return new ProcessingResultBuilder();
    }

    public static class ProcessingResultBuilder {
        private final List<ProcessingProblem> problems = new ArrayList<>();

        public ProcessingResultBuilder withProblem(ProcessingProblem problem) {
            problems.add(problem);
            return this;
        }

        public ProcessingResult build() {
            return new ProcessingResult(problems);
        }
    }
}
