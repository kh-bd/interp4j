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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationProcessor extends VoidVisitorAdapter<Void> {

    private final SExpressionParser parser = new SExpressionParser();
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

        SExpression sExpr = parser.parse(stringLiteral.asString()).orElse(null);
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
        methodCall.setName("interpolate");
        methodCall.setArguments(toExpressionsArray(sExpr.getExpressions()));
        methodCall.setScope(makeReceiver(sExpr.getParts()));
    }

    private NodeList<Expression> toExpressionsArray(List<String> sExpressions) {
        List<Expression> expressions = sExpressions.stream()
                .map(StaticJavaParser::<Expression>parseExpression)
                .collect(Collectors.toList());
        return new NodeList<>(expressions);
    }

    private Expression makeReceiver(List<String> parts) {
        ObjectCreationExpr objectCreationExpr = StaticJavaParser.parseExpression("new Object()");
        objectCreationExpr.setType(SInterpolator.class.getCanonicalName());
        objectCreationExpr.setArguments(toStringLiteralsArray(parts));
        return objectCreationExpr;
    }

    private NodeList<Expression> toStringLiteralsArray(List<String> parts) {
        List<Expression> literals = parts.stream()
                .map(StringLiteralExpr::new)
                .collect(Collectors.toList());
        return new NodeList<>(literals);
    }

    private Range getRange(Expression expr) {
        return expr.getRange().orElse(null);
    }
}
