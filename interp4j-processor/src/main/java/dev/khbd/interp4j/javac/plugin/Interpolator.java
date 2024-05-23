package dev.khbd.interp4j.javac.plugin;

import com.sun.tools.javac.tree.JCTree;

/**
 * Extension point to implement interpolation logic.
 *
 * @author Sergei Khadanovich
 */
public interface Interpolator {

    /**
     * Check if method call is interpolation call.
     *
     * @param invocation method invocation tree
     */
    boolean isInterpolateCall(JCTree.JCMethodInvocation invocation);

    /**
     * Interpolate method call.
     *
     * @param invocation method invocation tree
     * @return {@literal null} if interpolation failed
     */
    JCTree.JCExpression interpolate(JCTree.JCMethodInvocation invocation);
}
