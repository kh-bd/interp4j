package dev.khbd.interp4j.javac.plugin.s;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Sergei_Khadanovich
 */
class SImports {

    final Set<SImport> imports = EnumSet.noneOf(SImport.class);

    void add(SImport sImport) {
        imports.add(sImport);
    }

    boolean isStaticMethodImportPresent() {
        return imports.contains(SImport.METHOD);
    }

    boolean isClassImportPresent() {
        return imports.contains(SImport.CLASS);
    }
}
