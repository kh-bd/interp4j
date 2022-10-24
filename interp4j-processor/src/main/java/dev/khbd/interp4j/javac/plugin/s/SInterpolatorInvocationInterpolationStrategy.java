package dev.khbd.interp4j.javac.plugin.s;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import dev.khbd.interp4j.javac.plugin.s.expr.ExpressionPart;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpression;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionPart;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionVisitor;
import dev.khbd.interp4j.javac.plugin.s.expr.TextPart;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * Interpolation strategy which instantiates `SInterpolator` instance.
 *
 * @author Sergei_Khadanovich
 */
@RequiredArgsConstructor
class SInterpolatorInvocationInterpolationStrategy implements InterpolationStrategy {

    private final TreeMaker treeMaker;
    private final ParserFactory parserFactory;
    private final Names symbolsTable;

    @Override
    public JCTree.JCExpression interpolate(SExpression sExpr, int basePosition) {
        ArgumentsCollector argumentsCollector = new ArgumentsCollector(basePosition);
        sExpr.visit(argumentsCollector);

        JCTree.JCNewClass jcNew = treeMaker
                .NewClass(null, List.nil(),
                        select("dev.khbd.interp4j.core.internal.s.SInterpolator"),
                        argumentsCollector.constructorArguments,
                        null
                );
        return treeMaker.at(basePosition).Apply(List.nil(),
                treeMaker.Select(jcNew, symbolsTable.fromString("interpolate")),
                argumentsCollector.methodArguments);
    }

    private JCTree.JCExpression select(String path) {
        String[] parts = path.split("\\.");
        JCTree.JCExpression result = treeMaker.Ident(symbolsTable.fromString(parts[0]));
        for (int i = 1; i < parts.length; i++) {
            result = treeMaker.Select(result, symbolsTable.fromString(parts[i]));
        }
        return result;
    }

    @RequiredArgsConstructor
    private class ArgumentsCollector implements SExpressionVisitor {

        private final int basePosition;

        private List<JCTree.JCExpression> constructorArguments = List.nil();
        private List<JCTree.JCExpression> methodArguments = List.nil();

        private SExpressionPart lastPart;

        @Override
        public void visitExpressionPart(ExpressionPart expressionPart) {
            if (Objects.isNull(lastPart) || lastPart.isExpression()) {
                constructorArguments = constructorArguments.append(treeMaker.Literal(""));
            }
            lastPart = expressionPart;

            JavacParser parser = parserFactory.newParser(
                    expressionPart.getExpression(), false,
                    false, false);
            JCTree.JCExpression expr = parser.parseExpression();
            expr.pos = basePosition;
            methodArguments = methodArguments.append(expr);
        }

        @Override
        public void visitTextPart(TextPart textPart) {
            lastPart = textPart;
            constructorArguments = constructorArguments.append(treeMaker.Literal(textPart.getText()));
        }

        @Override
        public void finish() {
            if (lastPart.isExpression()) {
                constructorArguments = constructorArguments.append(treeMaker.Literal(""));
            }
        }
    }
}
