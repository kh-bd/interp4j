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
    private boolean errorOccurred;

    @Override
    public void report(Range range, String message, MessageType type) {
        if (type == MessageType.ERROR) {
            errorOccurred = true;
        }
        base.report(range, message, type);
    }
}
