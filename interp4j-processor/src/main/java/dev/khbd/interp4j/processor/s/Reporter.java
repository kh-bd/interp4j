package dev.khbd.interp4j.processor.s;

import com.github.javaparser.Range;

/**
 * Hook which will be invoked to report any messages during processing.
 *
 * @author Sergei_Khadanovich
 */
public interface Reporter {

    /**
     * Report message with specified type.
     *
     * @param range   source code range
     * @param message message
     * @param type    message type
     */
    void report(Range range, String message, MessageType type);

    /**
     * Report error message.
     *
     * @param range   source code range
     * @param message message
     */
    default void reportError(Range range, String message) {
        report(range, message, MessageType.ERROR);
    }

    /**
     * Report warn message.
     *
     * @param range   source code range
     * @param message message
     */
    default void reportWarn(Range range, String message) {
        report(range, message, MessageType.WARN);
    }

    /**
     * Report info message.
     *
     * @param range   source code range
     * @param message message
     */
    default void reportInfo(Range range, String message) {
        report(range, message, MessageType.INFO);
    }
}
