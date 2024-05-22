package dev.khbd.interp4j.javac.plugin.s;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Interpolation type.
 *
 * @author Sergei Khadanovich
 */
@Getter
@RequiredArgsConstructor
enum Interpolation {

    S("dev.khbd.interp4j.core", "Interpolations", "s");

    private final String packageName;
    private final String className;
    private final String method;
}
