package dev.khbd.interp4j.javac.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interpolation type.
 *
 * @author Sergei Khadanovich
 */
@Getter
@RequiredArgsConstructor
public enum Interpolation {

    S("dev.khbd.interp4j.core", "Interpolations", "s"),
    FMT("dev.khbd.interp4j.core", "Interpolations", "fmt");

    private final String packageName;
    private final String className;
    private final String method;
}
