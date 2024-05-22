package dev.khbd.interp4j.javac.plugin.s;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Interpolate method imports.
 *
 * @author Sergei Khadanovich
 */
@ToString
@RequiredArgsConstructor
class Imports {

    private final Set<ImportType> imports;

    boolean isSimpleMethodCallAllowed() {
        return imports.contains(ImportType.METHOD) || imports.contains(ImportType.CLASS_WILDCARD);
    }

    boolean isQualifiedMethodCallAllowed() {
        return imports.contains(ImportType.CLASS) || imports.contains(ImportType.PACKAGE_WILDCARD);
    }

    /**
     * Create empty imports' collector.
     *
     * @param interpolation interpolation type
     */
    static ImportsCollector collector(Interpolation interpolation) {
        return new ImportsCollectorImpl(interpolation);
    }

    /**
     * Import type.
     */
    private enum ImportType {

        /**
         * Interpolation class is imported.
         *
         * <p>
         * For example, {@code import dev.khbd.interp4j.core.Interpolations;}
         */
        CLASS {
            @Override
            ImportTypePredicate buildPredicate(Interpolation interpolation) {
                return new ImportTypePredicate(this, PluginUtils.pathPredicate(interpolation.getPackageName() + "." + interpolation.getClassName()));
            }
        },

        /**
         * Static import with exact method name.
         *
         * <p>
         * For example, {@code import static dev.khbd.interp4j.core.Interpolations.s;}
         */
        METHOD {
            @Override
            ImportTypePredicate buildPredicate(Interpolation interpolation) {
                return new ImportTypePredicate(this, PluginUtils.pathPredicate(interpolation.getPackageName() + "." + interpolation.getClassName() + "." + interpolation.getMethod()));
            }
        },

        /**
         * Static import with wildcard sign.
         *
         * <p>
         * For example, {@code import static dev.khbd.interp4j.core.Interpolations.*;}.
         */
        CLASS_WILDCARD {
            @Override
            ImportTypePredicate buildPredicate(Interpolation interpolation) {
                return new ImportTypePredicate(this, PluginUtils.pathPredicate(interpolation.getPackageName() + "." + interpolation.getClassName() + ".*"));
            }
        },

        /**
         * Import with wildcard sign.
         *
         * <p>
         * For example, {@code import dev.khbd.interp4j.core.*;}.
         */
        PACKAGE_WILDCARD {
            @Override
            ImportTypePredicate buildPredicate(Interpolation interpolation) {
                return new ImportTypePredicate(this, PluginUtils.pathPredicate(interpolation.getPackageName() + ".*"));
            }
        };

        abstract ImportTypePredicate buildPredicate(Interpolation interpolation);
    }

    @RequiredArgsConstructor
    private static final class ImportTypePredicate implements Predicate<Tree> {

        @Getter
        private final Imports.ImportType importType;
        private final Predicate<Tree> delegate;

        @Override
        public boolean test(Tree tree) {
            return delegate.test(tree);
        }
    }

    private static final class ImportsCollectorImpl implements ImportsCollector {

        private final List<ImportTypePredicate> predicates;

        ImportsCollectorImpl(Interpolation interpolation) {
            predicates = Arrays.stream(Imports.ImportType.values())
                    .map(type -> type.buildPredicate(interpolation))
                    .toList();
        }

        @Override
        public Imports collect(List<? extends ImportTree> trees) {
            Set<Imports.ImportType> imports = new HashSet<>();
            for (ImportTree tree : trees) {
                for (ImportTypePredicate predicate : predicates) {
                    if (predicate.test(tree.getQualifiedIdentifier())) {
                        imports.add(predicate.getImportType());
                    }
                }
            }
            return new Imports(imports);
        }
    }
}

