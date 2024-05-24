package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Abstract interpolation factory.
 *
 * @author Sergei Khadanovich
 */
@RequiredArgsConstructor
abstract class AbstractInterpolatorFactory implements InterpolatorFactory {

    /**
     * Interpolation type.
     */
    protected final Interpolation interpolation;

    /**
     * Base class for an interpolator.
     */
    abstract protected class AbstractInterpolator<E> implements Interpolator {

        private final Predicate<Tree> METHOD_PREDICATE = PluginUtils.pathPredicate(interpolation.getMethod());
        private final Predicate<Tree> QUALIFIED_METHOD_PREDICATE =
                PluginUtils.pathPredicate(interpolation.getClassName(), interpolation.getMethod());
        private final Predicate<Tree> FQN_PREDICATE =
                PluginUtils.pathPredicate(interpolation.getPackageName(), interpolation.getClassName(), interpolation.getMethod());

        private final Imports imports;

        protected AbstractInterpolator(CompilationUnitTree unit) {
            this.imports = Imports.collector(interpolation).collect(unit.getImports());
        }

        @Override
        public boolean isInterpolateCall(JCTree.JCMethodInvocation invocation) {
            if (!isInterpolateCall(invocation.getMethodSelect())) {
                return false;
            }
            List<? extends ExpressionTree> arguments = invocation.getArguments();
            // otherwise, it's a compile-time error
            return arguments.size() == 1;
        }

        @Override
        public Result<List<Message>, JCTree.JCExpression> interpolate(JCTree.JCMethodInvocation invocation) {
            JCTree.JCExpression firstArgument = invocation.getArguments().get(0);
            if (firstArgument.getKind() != Tree.Kind.STRING_LITERAL) {
                return Result.error(List.of(new Message("non.string.literal", firstArgument)));
            }

            JCTree.JCLiteral literalTree = (JCTree.JCLiteral) firstArgument;
            String literal = (String) literalTree.getValue();

            E expression = parse(literal);
            if (Objects.isNull(expression)) {
                return Result.error(List.of(new Message("wrong.expression.format", firstArgument)));
            }

            List<Message> errors = validate(literalTree, expression);
            if (!errors.isEmpty()) {
                return Result.error(errors);
            }

            return Result.success(interpolate(invocation, literal, expression));
        }

        /**
         * Parse string literal to expression model.
         *
         * @param literal string literal
         */
        abstract protected E parse(String literal);

        /**
         * Validate expression for correctness.
         */
        protected List<Message> validate(JCTree.JCLiteral literal, E expression) {
            return List.of();
        }

        /**
         * Interpolate valid expression.
         *
         * @param invocation original method invocation
         * @param literal    string literal
         * @param expression parsed expression
         */
        abstract protected JCTree.JCExpression interpolate(JCTree.JCMethodInvocation invocation,
                                                           String literal, E expression);

        private boolean isInterpolateCall(ExpressionTree methodSelect) {
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
    }
}
