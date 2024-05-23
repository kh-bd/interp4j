package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.util.Context;

/**
 * Interpolator factory.
 *
 * @author Sergei Khadanovich
 */
interface InterpolatorFactory {

    /**
     * Create new interpolator.
     *
     * <p>New interpolator is going to be created for each compilation unit.
     *
     * @param context compilation context
     * @param unit    compilation unit
     * @return interpolator instance
     */
    Interpolator create(Context context, CompilationUnitTree unit);
}
