package dev.khbd.interp4j.processor.s;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.khbd.interp4j.core.Interpolations;
import dev.khbd.interp4j.core.internal.s.SInterpolator;
import dev.khbd.interp4j.processor.s.expr.ExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpression;
import dev.khbd.interp4j.processor.s.expr.SExpressionParser;
import dev.khbd.interp4j.processor.s.expr.SExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpressionVisitor;
import dev.khbd.interp4j.processor.s.expr.TextPart;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public final class SInterpolationProcessor {

    private static final String S_METHOD_NAME = "s";

    private static final SInterpolationProcessor INSTANCE = new SInterpolationProcessor();

    /**
     * Get instance of processor.
     *
     * @return instance of processor
     */
    public static SInterpolationProcessor getInstance() {
        return INSTANCE;
    }

    private SInterpolationProcessor() {
    }

    /**
     * Process compilation unit.
     *
     * @param unit     compilation unit
     * @param reporter message reporter
     */
    public void process(@NonNull CompilationUnit unit, @NonNull Reporter reporter) {
        ErrorDetectingReporter errorDetectingReporter = wrapReporter(reporter);

        processSInvocations(unit, errorDetectingReporter);

        if (!errorDetectingReporter.isAnyErrorOccur()) {
            removeAllInterpolationsImports(unit);
        }
    }

    private ErrorDetectingReporter wrapReporter(Reporter reporter) {
        if (reporter instanceof ErrorDetectingReporter) {
            return (ErrorDetectingReporter) reporter;
        }
        return new ErrorDetectingReporter(reporter);
    }

    private void processSInvocations(CompilationUnit unit, Reporter reporter) {
        SImports sImports = resolveImports(unit);
        unit.accept(new SMethodCallProcessor(reporter, sImports), null);
    }

    private void removeAllInterpolationsImports(CompilationUnit unit) {
        List<ImportDeclaration> forRemoval = unit.getImports().stream()
                .filter(SInterpolationProcessor::isInterpolationsImport)
                .collect(Collectors.toList());
        unit.getImports().removeAll(forRemoval);
    }

    @RequiredArgsConstructor
    private static class SMethodCallProcessor extends VoidVisitorAdapter<Object> {

        final Reporter reporter;
        final SImports imports;

        @Override
        public void visit(MethodCallExpr methodCall, Object arg) {
            super.visit(methodCall, arg);

            if (!isInterpolatorCall(methodCall)) {
                return;
            }

            StringLiteralExpr stringLiteral = getFirstArgumentStringLiteral(methodCall);
            if (Objects.isNull(stringLiteral)) {
                return;
            }

            SExpression sExpr = SExpressionParser.getInstance().parse(stringLiteral.asString()).orElse(null);
            if (Objects.isNull(sExpr)) {
                reporter.reportError(getRange(stringLiteral), "Wrong expression format");
                return;
            }

            substituteInvocation(sExpr, methodCall);
        }

        boolean isInterpolatorCall(MethodCallExpr methodCall) {
            String methodName = methodCall.getName().asString();
            if (!methodName.equals(S_METHOD_NAME)) {
                return false;
            }

            Expression scopeExpr = methodCall.getScope().orElse(null);

            // s() call
            if (scopeExpr == null) {
                return imports.isStaticMethodImportPresent();
            }

            // Interpolations.s() call
            if (scopeExpr instanceof NameExpr) {
                String scopeString = scopeExpr.asNameExpr().getName().asString();
                if (scopeString.equals(Interpolations.class.getSimpleName())) {
                    return imports.isClassImportPresent();
                }
            }

            // FQN.s() call
            if (scopeExpr.toString().equals(Interpolations.class.getCanonicalName())) {
                return true;
            }

            return false;
        }

        StringLiteralExpr getFirstArgumentStringLiteral(MethodCallExpr methodCall) {
            int argumentsCount = methodCall.getArguments().size();

            // seems like it is a compile-time error
            if (argumentsCount != 1) {
                return null;
            }

            Expression argument = methodCall.getArgument(0);
            if (!argument.isStringLiteralExpr()) {
                reporter.reportError(getRange(argument), "Only string literal value is supported");
                return null;
            }

            return argument.asStringLiteralExpr();
        }

        void substituteInvocation(SExpression sExpr, MethodCallExpr methodCall) {
            ArgumentsCollector expressionsCollector = new ArgumentsCollector();
            sExpr.visit(expressionsCollector);

            methodCall.setName("interpolate");
            methodCall.setArguments(expressionsCollector.methodArguments);
            methodCall.setScope(makeReceiver(expressionsCollector));
        }

        Expression makeReceiver(ArgumentsCollector expressionsCollector) {
            ObjectCreationExpr objectCreationExpr = StaticJavaParser.parseExpression("new Object()");
            objectCreationExpr.setType(SInterpolator.class.getCanonicalName());
            objectCreationExpr.setArguments(expressionsCollector.constructorArguments);
            return objectCreationExpr;
        }

        Range getRange(Expression expr) {
            return expr.getRange().orElse(null);
        }
    }

    private static SImports resolveImports(CompilationUnit unit) {
        SImports imports = new SImports();
        for (ImportDeclaration importDecl : unit.getImports()) {
            if (isInterpolationsImport(importDecl)) {
                imports.add(SImport.of(importDecl.isStatic()));
            }
        }
        return imports;
    }

    private static boolean isInterpolationsImport(ImportDeclaration importDecl) {
        String importString = importDecl.getName().asString();
        return importString.startsWith(Interpolations.class.getCanonicalName());
    }

    private static class SImports {

        final Set<SImport> imports = EnumSet.noneOf(SImport.class);

        void add(SImport sImport) {
            imports.add(sImport);
        }

        boolean isStaticMethodImportPresent() {
            return imports.contains(SImport.METHOD);
        }

        boolean isClassImportPresent() {
            return imports.contains(SImport.CLASS);
        }
    }

    /**
     * Kind of `s` method import.
     */
    private enum SImport {
        CLASS,
        METHOD;

        static SImport of(boolean isStatic) {
            if (isStatic) {
                return METHOD;
            }
            return CLASS;
        }
    }

    private static class ArgumentsCollector implements SExpressionVisitor {

        private final NodeList<Expression> constructorArguments = new NodeList<>();
        private final NodeList<Expression> methodArguments = new NodeList<>();

        private SExpressionPart lastPart;

        @Override
        public void visitExpressionPart(ExpressionPart expressionPart) {
            if (Objects.isNull(lastPart) || lastPart.isExpression()) {
                constructorArguments.add(new StringLiteralExpr(""));
            }
            lastPart = expressionPart;
            methodArguments.add(StaticJavaParser.parseExpression(expressionPart.getExpression()));
        }

        @Override
        public void visitTextPart(TextPart textPart) {
            lastPart = textPart;
            constructorArguments.add(new StringLiteralExpr(textPart.getText()));
        }

        @Override
        public void finish() {
            if (lastPart.isExpression()) {
                constructorArguments.add(new StringLiteralExpr(""));
            }
        }
    }
}
