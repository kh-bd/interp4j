package dev.khbd.interp4j.processor.s;

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
public class SInterpolatorProcessor extends VoidVisitorAdapter<Void> {

    private final SExpressionParser parser = new SExpressionParser();
    private final JavaParserFacade javaParserFacade;

    public SInterpolatorProcessor(TypeSolver typeSolver) {
        this.javaParserFacade = JavaParserFacade.get(typeSolver);
    }

    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
        if (!isInterpolatorCall(methodCall)) {
            return;
        }

        String exprAsString = getFirstArgumentStringLiteral(methodCall);
        if (Objects.isNull(exprAsString)) {
            return;
        }

        parser.parse(exprAsString)
                .ifPresent(expr -> substituteInvocation(expr, methodCall));
    }

    private String getFirstArgumentStringLiteral(MethodCallExpr methodCall) {
        int argumentsCount = methodCall.getArguments().size();

        // seems like it is a compile-time error
        if (argumentsCount != 1) {
            return null;
        }

        Expression argument = methodCall.getArgument(0);
        if (!argument.isStringLiteralExpr()) {
            // todo: only string literal values are supported
            return null;
        }

        return argument.asStringLiteralExpr().asString();
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
}
