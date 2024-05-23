package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.ImportTree;

import java.util.List;

/**
 * Import collector.
 *
 * @author Sergei Khadanovich
 */
public interface ImportsCollector {

    /**
     * Collect all import infos
     *
     * @param importTrees compilation unit's imports
     * @return imports
     */
    Imports collect(List<? extends ImportTree> importTrees);
}
