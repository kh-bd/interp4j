package dev.khbd.interp4j.javac.plugin;

/**
 * Kind of 's' function import.
 */
enum SImport {
    CLASS,
    METHOD;

    static SImport of(boolean isStatic) {
        if (isStatic) {
            return METHOD;
        }
        return CLASS;
    }
}
