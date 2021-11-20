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
}
