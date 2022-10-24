package dev.khbd.interp4j.javac.plugin.s;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LambdaExpressionTree.BodyKind;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.YieldTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpression;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionParser;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationPlugin implements Plugin {

    @Override
    public String getName() {
        return "interp4j";
    }

    @Override
    public void init(JavacTask task, String... args) {
        Options options = new Options(args);
        task.addTaskListener(new TaskListener() {
            @Override
            public void finished(TaskEvent event) {
                if (event.getKind() != TaskEvent.Kind.PARSE) {
                    return;
                }

                CompilationUnitTree unit = event.getCompilationUnit();
                Context context = ((BasicJavacTask) task).getContext();

                BundleInitializer.initPluginBundle(context);

                SInterpolationTreeScanner interpolator = new SInterpolationTreeScanner(context);
                unit.accept(interpolator, null);

                if (interpolator.interpolationTakePlace && options.prettyPrintAfterInterpolationEnabled()) {
                    prettyPrintTree(((JCTree.JCCompilationUnit) unit));
                }
            }
        });
    }

    private static void prettyPrintTree(JCTree tree) {
        OutputStreamWriter writer = new OutputStreamWriter(System.out);

        Pretty pretty = new Pretty(writer, true);
        tree.accept(pretty);

        try {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static class SInterpolationTreeScanner extends TreeScanner<Void, Void> {

        final SImports imports;
        final InterpolationStrategy interpolationStrategy;
        final TreeMaker treeMaker;
        final Log logger;

        @Getter
        boolean interpolationTakePlace = false;

        private SInterpolationTreeScanner(Context context) {
            this.imports = new SImports();
            this.treeMaker = TreeMaker.instance(context);
            this.logger = Log.instance(context);
            this.interpolationStrategy = new SInterpolatorInvocationInterpolationStrategy(treeMaker, ParserFactory.instance(context), Names.instance(context));
        }

        @Override
        public Void visitImport(ImportTree importTree, Void unused) {
            super.visitImport(importTree, unused);

            if (isInterpolationsImport(importTree)) {
                imports.add(SImport.of(importTree.isStatic()));
            }

            return null;
        }

        private boolean isInterpolationsImport(ImportTree importTree) {
            return PluginUtils.IS_FQN_INTERPOLATIONS_S
                    .or(PluginUtils.IS_FQN_INTERPOLATIONS)
                    .test(importTree.getQualifiedIdentifier());
        }

        @Override
        public Void visitParenthesized(ParenthesizedTree tree, Void unused) {
            super.visitParenthesized(tree, unused);

            JCTree.JCParens parens = (JCTree.JCParens) tree;
            interpolateIfNeeded(parens.expr, ie -> parens.expr = ie);

            return null;
        }

        @Override
        public Void visitVariable(VariableTree tree, Void unused) {
            super.visitVariable(tree, unused);

            JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl) tree;
            if (Objects.isNull(varDecl.init)) {
                return null;
            }

            interpolateIfNeeded(varDecl.init, ie -> varDecl.init = ie);

            return null;
        }

        @Override
        public Void visitAssignment(AssignmentTree tree, Void unused) {
            super.visitAssignment(tree, unused);

            JCTree.JCAssign assignment = (JCTree.JCAssign) tree;
            interpolateIfNeeded(assignment.rhs, ie -> assignment.rhs = ie);

            return null;
        }

        @Override
        public Void visitYield(YieldTree tree, Void unused) {
            super.visitYield(tree, unused);

            JCTree.JCYield jcYield = (JCTree.JCYield) tree;
            interpolateIfNeeded(jcYield.value, ie -> jcYield.value = ie);

            return null;
        }

        // some wierd behavior is noticed.
        //
        // JCCase#body is not null and contains expected method invocation if kind == RULE (as docs says),
        // but changing body to something else doesn't lead to changing AST tree.
        // It seems that other parts of AST processing machinery don't take body into account.
        // For example, Pretty#visitCase method takes into account only stats list and ignores body field at all.

        // It seems expressions after right arrow are rewritten to yield statements:
        // For example,
        // case label -> expression;
        // will be rewritten to
        // case label -> yield expression;
        @Override
        public Void visitCase(CaseTree tree, Void unused) {
            JCTree.JCCase jcCase = (JCTree.JCCase) tree;

            for (JCTree.JCCaseLabel label : jcCase.getLabels()) {
                label.accept(this, unused);
            }

            for (StatementTree stat : jcCase.stats) {
                stat.accept(this, unused);
            }

            return null;
        }

        @Override
        public Void visitExpressionStatement(ExpressionStatementTree tree, Void unused) {
            super.visitExpressionStatement(tree, unused);

            JCTree.JCExpressionStatement jcExprStat = (JCTree.JCExpressionStatement) tree;
            interpolateIfNeeded(jcExprStat.expr, ie -> jcExprStat.expr = ie);

            return null;
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree tree, Void unused) {
            super.visitCompoundAssignment(tree, unused);

            JCTree.JCAssignOp assignment = (JCTree.JCAssignOp) tree;
            interpolateIfNeeded(assignment.rhs, ie -> assignment.rhs = ie);

            return null;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, Void unused) {
            super.visitMethodInvocation(tree, unused);

            JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) tree;
            methodInvocation.args = interpolateIfNeeded(methodInvocation.args);

            JCTree.JCExpression methodSelect = methodInvocation.meth;
            if (methodSelect.getKind() == Tree.Kind.MEMBER_SELECT) {
                JCTree.JCFieldAccess jcFieldAccess = (JCTree.JCFieldAccess) methodSelect;
                interpolateIfNeeded(jcFieldAccess.selected, ie -> jcFieldAccess.selected = ie);
            }

            return null;
        }

        @Override
        public Void visitNewClass(NewClassTree tree, Void unused) {
            super.visitNewClass(tree, unused);

            JCTree.JCNewClass jcNewClass = (JCTree.JCNewClass) tree;
            jcNewClass.args = interpolateIfNeeded(jcNewClass.args);

            return null;
        }

        @Override
        public Void visitMemberReference(MemberReferenceTree tree, Void unused) {
            super.visitMemberReference(tree, unused);

            JCTree.JCMemberReference memberRef = (JCTree.JCMemberReference) tree;
            interpolateIfNeeded(memberRef.expr, ie -> memberRef.expr = ie);

            return null;
        }

        @Override
        public Void visitReturn(ReturnTree tree, Void unused) {
            super.visitReturn(tree, unused);

            JCTree.JCReturn jcReturn = (JCTree.JCReturn) tree;
            if (Objects.nonNull(jcReturn.expr)) {
                interpolateIfNeeded(jcReturn.expr, ie -> jcReturn.expr = ie);
            }

            return null;
        }

        @Override
        public Void visitLambdaExpression(LambdaExpressionTree tree, Void unused) {
            super.visitLambdaExpression(tree, unused);

            JCTree.JCLambda jcLambda = (JCTree.JCLambda) tree;
            if (jcLambda.getBodyKind() == BodyKind.EXPRESSION) {
                interpolateIfNeeded((JCTree.JCExpression) jcLambda.body, ie -> jcLambda.body = ie);
            }

            return null;
        }

        @Override
        public Void visitConditionalExpression(ConditionalExpressionTree tree, Void unused) {
            super.visitConditionalExpression(tree, unused);

            JCTree.JCConditional conditional = (JCTree.JCConditional) tree;
            interpolateIfNeeded(conditional.truepart, ie -> conditional.truepart = ie);
            interpolateIfNeeded(conditional.falsepart, ie -> conditional.falsepart = ie);

            return null;
        }

        @Override
        public Void visitBinary(BinaryTree tree, Void unused) {
            super.visitBinary(tree, unused);

            JCTree.JCBinary binary = (JCTree.JCBinary) tree;
            interpolateIfNeeded(binary.lhs, ie -> binary.lhs = ie);
            interpolateIfNeeded(binary.rhs, ie -> binary.rhs = ie);

            return null;
        }

        private List<JCTree.JCExpression> interpolateIfNeeded(List<JCTree.JCExpression> expressions) {
            List<JCTree.JCExpression> result = List.nil();

            for (JCTree.JCExpression expr : expressions) {
                JCTree.JCExpression interpolated = interpolateIfNeeded(expr);
                if (Objects.nonNull(interpolated)) {
                    result = result.append(interpolated);
                    interpolationTakePlace = true;
                } else {
                    result = result.append(expr);
                }
            }

            return result;
        }

        private void interpolateIfNeeded(JCTree.JCExpression expression,
                                         Consumer<? super JCTree.JCExpression> resultConsumer) {
            JCTree.JCExpression interpolated = interpolateIfNeeded(expression);
            if (Objects.nonNull(interpolated)) {
                resultConsumer.accept(interpolated);
                interpolationTakePlace = true;
            }
        }

        private JCTree.JCExpression interpolateIfNeeded(JCTree.JCExpression expression) {
            if (!PluginUtils.isSMethodInvocation(expression, imports)) {
                return null;
            }
            ExpressionTree firstArgument = getFirstArgument(expression);
            if (firstArgument.getKind() != Tree.Kind.STRING_LITERAL) {
                logger.error(expression.pos(), new JCDiagnostic.Error("compiler", "non.string.literal"));
                return null;
            }

            LiteralTree literalTree = (LiteralTree) firstArgument;
            String literal = (String) literalTree.getValue();

            SExpression sExpr = SExpressionParser.getInstance().parse(literal).orElse(null);
            if (Objects.isNull(sExpr)) {
                logger.error(expression.pos(), new JCDiagnostic.Error("compiler", "wrong.expression.format"));
                return null;
            }

            return interpolate(literal, sExpr, expression.pos);
        }

        private ExpressionTree getFirstArgument(ExpressionTree tree) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            return methodInvocation.getArguments().get(0);
        }

        private JCTree.JCExpression interpolate(String literal, SExpression sExpr, int basePosition) {
            if (!sExpr.hasAnyExpression()) {
                // It means, `s` expression doesn't contain any string parts,
                // so we can replace our method call with original string literal
                return interpolateWithoutExpressions(literal, basePosition);
            }
            return interpolateWithExpressions(sExpr, basePosition);
        }

        private JCTree.JCExpression interpolateWithoutExpressions(String literal,
                                                                  int basePosition) {
            return treeMaker.at(basePosition).Literal(literal);
        }

        private JCTree.JCExpression interpolateWithExpressions(SExpression sExpr, int basePosition) {
            return interpolationStrategy.interpolate(sExpr, basePosition);
        }

    }
}
