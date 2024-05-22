package dev.khbd.interp4j.javac.plugin.s;

import com.sun.source.tree.ImportTree;

import java.util.List;

/**
 * Import collector.
 *
 * @author Sergei Khadanovich
 */
interface ImportsCollector {

    /**
     * Collect all import infos
     *
     * @param importTrees compilation unit's imports
     * @return imports
     */
    Imports collect(List<? extends ImportTree> importTrees);
}
