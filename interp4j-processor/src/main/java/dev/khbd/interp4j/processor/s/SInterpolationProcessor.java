package dev.khbd.interp4j.processor.s;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import dev.khbd.interp4j.core.Interpolations;
import dev.khbd.interp4j.core.internal.s.SInterpolator;
import dev.khbd.interp4j.processor.s.expr.ExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpression;
import dev.khbd.interp4j.processor.s.expr.SExpressionParser;
import dev.khbd.interp4j.processor.s.expr.SExpressionPart;
import dev.khbd.interp4j.processor.s.expr.SExpressionVisitor;
import dev.khbd.interp4j.processor.s.expr.TextPart;

import java.util.Objects;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationProcessor extends VoidVisitorAdapter<Void> {

    private final JavaParserFacade javaParserFacade;
    private final Reporter reporter;

    public SInterpolationProcessor(TypeSolver typeSolver, Reporter reporter) {
        this.javaParserFacade = JavaParserFacade.get(typeSolver);
        this.reporter = reporter;
    }

    public SInterpolationProcessor(TypeSolver typeSolver) {
        this(typeSolver, Reporter.ignoreReporter());
    }

    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
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

    private StringLiteralExpr getFirstArgumentStringLiteral(MethodCallExpr methodCall) {
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

    private boolean isInterpolatorCall(MethodCallExpr methodCall) {
        MethodUsage methodUsage = javaParserFacade.solveMethodAsUsage(methodCall);
        ResolvedMethodDeclaration declaration = methodUsage.getDeclaration();

        if (!declaration.isStatic() || !declaration.getName().equals("s")) {
            return false;
        }

        ResolvedReferenceTypeDeclaration typeDeclaration = methodUsage.declaringType();
        return typeDeclaration.getQualifiedName().equals(Interpolations.class.getCanonicalName());
    }

    private void substituteInvocation(SExpression sExpr, MethodCallExpr methodCall) {
        ExpressionsCollector expressionsCollector = new ExpressionsCollector();
        sExpr.visit(expressionsCollector);

        methodCall.setName("interpolate");
        methodCall.setArguments(expressionsCollector.methodArguments);
        methodCall.setScope(makeReceiver(expressionsCollector));
    }

    private Expression makeReceiver(ExpressionsCollector expressionsCollector) {
        ObjectCreationExpr objectCreationExpr = StaticJavaParser.parseExpression("new Object()");
        objectCreationExpr.setType(SInterpolator.class.getCanonicalName());
        objectCreationExpr.setArguments(expressionsCollector.constructorArguments);
        return objectCreationExpr;
    }

    private Range getRange(Expression expr) {
        return expr.getRange().orElse(null);
    }

    static class ExpressionsCollector implements SExpressionVisitor {

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
