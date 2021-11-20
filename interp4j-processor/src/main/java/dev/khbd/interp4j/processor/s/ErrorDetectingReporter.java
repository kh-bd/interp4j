package dev.khbd.interp4j.processor.s;

import com.github.javaparser.Range;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sergei_Khadanovich
 */
@RequiredArgsConstructor
public final class ErrorDetectingReporter implements Reporter {

    private final Reporter base;
    @Getter
    private boolean anyErrorOccur;

    @Override
    public void reportInfo(Range range, String message) {
        base.reportInfo(range, message);
    }

    @Override
    public void reportWarn(Range range, String message) {
        base.reportWarn(range, message);
    }

    @Override
    public void reportError(Range range, String message) {
        anyErrorOccur = true;
        base.reportError(range, message);
    }

    @Override
    public void report(Range range, String message, MessageType type) {
        if (type == MessageType.ERROR) {
            anyErrorOccur = true;
        }
        base.report(range, message, type);
    }
}
