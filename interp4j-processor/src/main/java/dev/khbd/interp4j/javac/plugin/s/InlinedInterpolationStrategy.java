package dev.khbd.interp4j.javac.plugin.s;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import dev.khbd.interp4j.javac.plugin.s.expr.ExpressionPart;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpression;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionVisitor;
import dev.khbd.interp4j.javac.plugin.s.expr.TextPart;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Interpolation strategy which replace `s` method invocation by `+` operator.
 *
 * @author Sergei_Khadanovich
 */
@RequiredArgsConstructor
class InlinedInterpolationStrategy implements InterpolationStrategy {

    private final TreeMaker treeMaker;
    private final ParserFactory parserFactory;

    @Override
    public JCTree.JCExpression interpolate(SExpression sExpr, int basePosition) {
        ArgumentsCollector argumentsCollector = new ArgumentsCollector(basePosition);
        sExpr.visit(argumentsCollector);
        return treeMaker.at(basePosition).Parens(combineWithBinaryPlus(argumentsCollector.arguments));
    }

    private JCTree.JCExpression combineWithBinaryPlus(List<JCTree.JCExpression> expressions) {
        if (Objects.isNull(expressions.tail)) {
            return expressions.head;
        }

        JCTree.JCExpression acc = expressions.head;
        List<JCTree.JCExpression> tail = expressions.tail;
        while (Objects.nonNull(tail) && Objects.nonNull(tail.head)) {
            acc = treeMaker.Binary(JCTree.Tag.PLUS, acc, tail.head);
            tail = tail.tail;
        }
        return acc;
    }

    @RequiredArgsConstructor
    private class ArgumentsCollector implements SExpressionVisitor {

        private final int basePosition;

        private List<JCTree.JCExpression> arguments = List.nil();

        @Override
        public void visitExpressionPart(ExpressionPart expressionPart) {
            JavacParser parser = parserFactory.newParser(
                    expressionPart.expression(), false,
                    false, false);
            JCTree.JCExpression expr = parser.parseExpression();
            expr.pos = basePosition;
            arguments = arguments.append(expr);
        }

        @Override
        public void visitTextPart(TextPart textPart) {
            arguments = arguments.append(treeMaker.Literal(textPart.text()));
        }

    }
}
