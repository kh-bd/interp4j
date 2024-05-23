package dev.khbd.interp4j.javac.plugin.fmt.expr;

import lombok.Value;

/**
 * Numeric index.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class NumericIndex implements Index {

    int position;
}
