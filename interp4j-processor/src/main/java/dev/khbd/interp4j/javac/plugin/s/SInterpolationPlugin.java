package dev.khbd.interp4j.javac.plugin.s;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import dev.khbd.interp4j.javac.plugin.s.expr.ExpressionPart;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpression;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionParser;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionPart;
import dev.khbd.interp4j.javac.plugin.s.expr.SExpressionVisitor;
import dev.khbd.interp4j.javac.plugin.s.expr.TextPart;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

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
                SInterpolationTreeScanner interpolator = new SInterpolationTreeScanner(context);
                unit.accept(interpolator , null);

                if (interpolator.interpolationTakePlace && options.prettyPrintAfterInterpolationEnabled()) {
                    prettyPrintCompilationUnit(unit);
                }
            }
        });
    }

    private void prettyPrintCompilationUnit(CompilationUnitTree unit) {
        JCTree.JCCompilationUnit jcUnit = (JCTree.JCCompilationUnit) unit;

        OutputStreamWriter writer = new OutputStreamWriter(System.out);

        Pretty pretty = new Pretty(writer, true);
        jcUnit.accept(pretty);

        try {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static class SInterpolationTreeScanner extends TreeScanner<Void, Void> {

        final SImports imports;

        final TreeMaker factory;
        final Names symbolsTable;
        final ParserFactory parserFactory;
        final Log logger;

        @Getter
        boolean interpolationTakePlace = false;

        private SInterpolationTreeScanner(Context context) {
            this.imports = new SImports();
            this.factory = TreeMaker.instance(context);
            this.symbolsTable = Names.instance(context);
            this.parserFactory = ParserFactory.instance(context);
            this.logger = Log.instance(context);
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
        public Void visitVariable(VariableTree tree, Void unused) {
            super.visitVariable(tree, unused);

            JCTree.JCVariableDecl varDecl = (JCTree.JCVariableDecl) tree;
            if (Objects.isNull(varDecl.init)) {
                return null;
            }

            JCTree.JCExpression interpolated = interpolateIfNeeded(varDecl.getInitializer());
            if (Objects.nonNull(interpolated)) {
                varDecl.init = interpolated;
                interpolationTakePlace = true;
            }

            return null;
        }

        @Override
        public Void visitAssignment(AssignmentTree tree, Void unused) {
            super.visitAssignment(tree, unused);

            JCTree.JCAssign assignment = (JCTree.JCAssign) tree;

            JCTree.JCExpression interpolated = interpolateIfNeeded(assignment.rhs);
            if (Objects.nonNull(interpolated)) {
                assignment.rhs = interpolated;
                interpolationTakePlace = true;
            }

            return null;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, Void unused) {
            super.visitMethodInvocation(tree, unused);

            JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) tree;
            methodInvocation.args = interpolateIfNeeded(methodInvocation.args);

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

        private JCTree.JCExpression interpolateIfNeeded(JCTree.JCExpression expression) {
            if (!PluginUtils.isSMethodInvocation(expression, imports)) {
                return null;
            }
            ExpressionTree firstArgument = getFirstArgument(expression);
            if (firstArgument.getKind() != Tree.Kind.STRING_LITERAL) {
                logger.rawError(expression.pos, "Only string literal value is supported");
                return null;
            }

            LiteralTree literalTree = (LiteralTree) firstArgument;
            String literal = (String) literalTree.getValue();

            SExpression sExpr = SExpressionParser.getInstance().parse(literal).orElse(null);
            if (Objects.isNull(sExpr)) {
                logger.rawError(expression.pos, "Wrong expression format");
                return null;
            }

            return interpolate(literal, sExpr);
        }

        private ExpressionTree getFirstArgument(ExpressionTree tree) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            return methodInvocation.getArguments().get(0);
        }

        private JCTree.JCExpression interpolate(String literal, SExpression sExpr) {
            if (!sExpr.hasAnyExpression()) {
                // It means, `s` expression doesn't contain any string parts,
                // so we can replace our method call with original string literal
                return interpolateWithoutExpressions(literal);
            }
            return interpolateWithExpressions(sExpr);
        }

        private JCTree.JCExpression interpolateWithoutExpressions(String literal) {
            return factory.Literal(literal);
        }

        private JCTree.JCExpression interpolateWithExpressions(SExpression sExpr) {
            ArgumentsCollector argumentsCollector = new ArgumentsCollector();
            sExpr.visit(argumentsCollector);

            JCTree.JCNewClass jcNew = factory.NewClass(null, List.nil(),
                    select("dev.khbd.interp4j.core.internal.s.SInterpolator"),
                    argumentsCollector.constructorArguments,
                    null
            );
            return factory.Apply(List.nil(),
                    factory.Select(jcNew, symbolsTable.fromString("interpolate")),
                    argumentsCollector.methodArguments);
        }

        private JCTree.JCExpression select(String path) {
            String[] parts = path.split("\\.");
            JCTree.JCExpression result = factory.Ident(symbolsTable.fromString(parts[0]));
            for (int i = 1; i < parts.length; i++) {
                result = factory.Select(result, symbolsTable.fromString(parts[i]));
            }
            return result;
        }

        private class ArgumentsCollector implements SExpressionVisitor {

            private List<JCTree.JCExpression> constructorArguments = List.nil();
            private List<JCTree.JCExpression> methodArguments = List.nil();

            private SExpressionPart lastPart;

            @Override
            public void visitExpressionPart(ExpressionPart expressionPart) {
                if (Objects.isNull(lastPart) || lastPart.isExpression()) {
                    constructorArguments = constructorArguments.append(factory.Literal(""));
                }
                lastPart = expressionPart;

                JavacParser parser = parserFactory.newParser(
                        expressionPart.getExpression(), false,
                        false, false);
                methodArguments = methodArguments.append(parser.parseExpression());
            }

            @Override
            public void visitTextPart(TextPart textPart) {
                lastPart = textPart;
                constructorArguments = constructorArguments.append(factory.Literal(textPart.getText()));
            }

            @Override
            public void finish() {
                if (lastPart.isExpression()) {
                    constructorArguments = constructorArguments.append(factory.Literal(""));
                }
            }
        }
    }
}
