package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.Value;

/**
 * Conversion part in format specifiers.
 *
 * <p>Conversion can be one or two symbol string. See {@link java.util.Formatter} documentation
 * for more details about allowed symbols and their meanings.
 *
 * @author Sergei_Khadanovich
 */
@Value
public class Conversion {

    String symbols;
}
