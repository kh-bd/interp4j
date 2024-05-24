package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.Value;

/**
 * Position in original format expression.
 *
 * @author Sergei Khadanovich
 */
@Value
public class Position {

    int start;
    int end;
}
