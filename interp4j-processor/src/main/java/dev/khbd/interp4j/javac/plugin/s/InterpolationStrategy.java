package dev.khbd.interp4j.javac.plugin.s;

import com.sun.tools.javac.tree.JCTree;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpression;

/**
 * Interpolation strategy.
 *
 * @author Sergei_Khadanovich
 */
interface InterpolationStrategy {

    /**
     * Make expression which will be inserted into AST instead of `s` method invocation.
     *
     * @param sExpr        parsed s-expression
     * @param basePosition `s` method position in AST
     * @return expression which should be inserted into AST
     */
    JCTree.JCExpression interpolate(SExpression sExpr, int basePosition);
}
