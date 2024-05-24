package dev.khbd.interp4j.javac.plugin.s;

import lombok.Value;

/**
 * Position in original s expression.
 *
 * @author Sergei Khadanovich
 */
@Value
public class Position {

    int start;
    int end;
}
