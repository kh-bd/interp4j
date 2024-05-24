package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.Log;
import dev.khbd.interp4j.javac.plugin.s.SCode;
import dev.khbd.interp4j.javac.plugin.s.SExpression;
import dev.khbd.interp4j.javac.plugin.s.SExpressionParser;
import dev.khbd.interp4j.javac.plugin.s.SExpressionVisitor;
import dev.khbd.interp4j.javac.plugin.s.SText;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Sergei Khadanovich
 */
class SInterpolatorFactoryImpl implements InterpolatorFactory {

    private static final Predicate<Tree> METHOD_PREDICATE = PluginUtils.pathPredicate(Interpolation.S.getMethod());
    private static final Predicate<Tree> QUALIFIED_METHOD_PREDICATE =
            PluginUtils.pathPredicate(Interpolation.S.getClassName(), Interpolation.S.getMethod());
    private static final Predicate<Tree> FQN_PREDICATE =
            PluginUtils.pathPredicate(Interpolation.S.getPackageName(), Interpolation.S.getClassName(), Interpolation.S.getMethod());

    @Override
    public Interpolator create(Context context, CompilationUnitTree unit) {
        Imports imports = Imports.collector(Interpolation.S).collect(unit.getImports());
        return new SInterpolator(imports, Log.instance(context), TreeMaker.instance(context), ParserFactory.instance(context));
    }

    @RequiredArgsConstructor
    private static class SInterpolator implements Interpolator {

        private final Imports imports;
        private final Log logger;
        private final TreeMaker treeMaker;
        private final ParserFactory parserFactory;

        @Override
        public boolean isInterpolateCall(JCTree.JCMethodInvocation invocation) {
            if (!isSMethodInvocation(invocation)) {
                return false;
            }

            List<? extends ExpressionTree> arguments = invocation.getArguments();

            // otherwise, it's a compile-time error
            return arguments.size() == 1;
        }

        private boolean isSMethodInvocation(MethodInvocationTree methodInvocation) {
            ExpressionTree methodSelect = methodInvocation.getMethodSelect();

            // s() method call
            if (METHOD_PREDICATE.test(methodSelect)) {
                return imports.isSimpleMethodCallAllowed();
            }

            // Interpolations.s() method call
            if (QUALIFIED_METHOD_PREDICATE.test(methodSelect)) {
                return imports.isQualifiedMethodCallAllowed();
            }

            // FQN.s() method call
            return FQN_PREDICATE.test(methodSelect);
        }

        @Override
        public JCTree.JCExpression interpolate(JCTree.JCMethodInvocation invocation) {
            ExpressionTree firstArgument = invocation.getArguments().get(0);
            if (firstArgument.getKind() != Tree.Kind.STRING_LITERAL) {
                logger.error(invocation.pos(), new JCDiagnostic.Error("compiler", "non.string.literal"));
                return null;
            }

            LiteralTree literalTree = (LiteralTree) firstArgument;
            String literal = (String) literalTree.getValue();

            SExpression sExpr = SExpressionParser.getInstance().parse(literal).orElse(null);
            if (Objects.isNull(sExpr)) {
                logger.error(invocation.pos(), new JCDiagnostic.Error("compiler", "wrong.expression.format"));
                return null;
            }

            return interpolate(literal, sExpr, invocation.pos);
        }

        private JCTree.JCExpression interpolate(String literal, SExpression sExpr, int basePosition) {
            if (!sExpr.hasAnyCodePart()) {
                // It means, `s` expression doesn't contain any string parts,
                // so we can replace our method call with original string literal
                return interpolateWithoutCodeParts(literal, basePosition);
            }
            return interpolateWithCodeParts(sExpr, basePosition);
        }

        private JCTree.JCExpression interpolateWithoutCodeParts(String literal,
                                                                int basePosition) {
            return treeMaker.at(basePosition).Literal(literal);
        }

        private JCTree.JCExpression interpolateWithCodeParts(SExpression sExpr, int basePosition) {
            ArgumentsCollector argumentsCollector = new ArgumentsCollector(basePosition);
            sExpr.visit(argumentsCollector);
            return treeMaker.at(basePosition).Parens(combineWithBinaryPlus(argumentsCollector.arguments));
        }

        private JCTree.JCExpression combineWithBinaryPlus(com.sun.tools.javac.util.List<JCTree.JCExpression> expressions) {
            if (Objects.isNull(expressions.tail)) {
                return expressions.head;
            }

            JCTree.JCExpression acc = expressions.head;
            com.sun.tools.javac.util.List<JCTree.JCExpression> tail = expressions.tail;
            while (Objects.nonNull(tail) && Objects.nonNull(tail.head)) {
                acc = treeMaker.Binary(JCTree.Tag.PLUS, acc, tail.head);
                tail = tail.tail;
            }
            return acc;
        }

        @RequiredArgsConstructor
        private class ArgumentsCollector implements SExpressionVisitor {

            private final int basePosition;

            private com.sun.tools.javac.util.List<JCTree.JCExpression> arguments = com.sun.tools.javac.util.List.nil();

            @Override
            public void visitCodePart(SCode code) {
                JavacParser parser = parserFactory.newParser(code.getExpression(), false, false, false);
                JCTree.JCExpression expr = parser.parseExpression();
                expr.pos = basePosition;
                arguments = arguments.append(expr);
            }

            @Override
            public void visitTextPart(SText text) {
                arguments = arguments.append(treeMaker.Literal(text.getText()));
            }

        }
    }
}
