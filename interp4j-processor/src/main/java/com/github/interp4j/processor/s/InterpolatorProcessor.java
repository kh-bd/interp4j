package com.github.interp4j.processor.s;

import com.github.interp4j.core.Interpolations;
import com.github.interp4j.core.internal.s.Interpolator;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Level;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sergei_Khadanovich
 */
public class InterpolatorProcessor extends AbstractProcessor<CtInvocation<?>> {

    private static final String S_METHOD_NAME = "s";
    private static final String INTERPOLATE_METHOD_NAME = "interpolate";

    private final ExpressionParser parser = new ExpressionParser();

    @Override
    public void process(CtInvocation<?> element) {
        if (!sInterpolationInvocation(element)) {
            return;
        }

        CtLiteral<String> literal = findFirstLiteralArgument(element);
        if (Objects.isNull(literal)) {
            return;
        }

        ParseResult parserResult = parser.parse(literal.getValue());

        substituteInvocation(element, parserResult);
    }

    private CtLiteral<String> findFirstLiteralArgument(CtInvocation<?> element) {
        List<CtExpression<?>> arguments = element.getArguments();

        // don't need more fine-grained size validation because compiler can guarantee that
        // argument list has only one element.
        if (arguments.size() != 1) {
            return null;
        }

        CtExpression<?> expression = arguments.get(0);
        if (!(expression instanceof CtLiteral<?>)) {
            element.getFactory().getEnvironment()
                    .report(this, Level.ERROR, element, "Only string literal value is supported!");
            return null;
        }

        CtLiteral<?> literal = (CtLiteral<?>) expression;
        Object value = literal.getValue();
        if (Objects.isNull(value)) {
            element.getFactory().getEnvironment()
                    .report(this, Level.ERROR, element, "Only string literal value is supported!");
            return null;
        }

        return (CtLiteral<String>) literal;
    }

    private boolean sInterpolationInvocation(CtInvocation<?> element) {
        return targetClassIsInterpolations(element) && functionIsS(element);
    }

    private boolean functionIsS(CtInvocation<?> element) {
        CtExecutableReference<?> executableRef = element.getExecutable();
        return executableRef.equals(findExecutable(Interpolations.class, S_METHOD_NAME));
    }

    private boolean targetClassIsInterpolations(CtInvocation<?> element) {
        CtExpression<?> target = element.getTarget();

        if (!(target instanceof CtTypeAccess<?>)) {
            return false;
        }

        CtTypeAccess<?> type = (CtTypeAccess<?>) target;
        CtTypeReference<?> typeRef = type.getAccessedType();

        return typeRef.equals(getFactory().createCtTypeReference(Interpolations.class));
    }

    private List<CtExpression<?>> interpolateArguments(List<String> expressions) {
        return expressions.stream()
                .map(expr -> getFactory().createCodeSnippetExpression(expr))
                .collect(Collectors.toList());
    }

    private CtExpression<?> interpolatorConstructorCall(List<String> parts) {
        CtTypeReference<?> typeRef = getFactory().createCtTypeReference(Interpolator.class);

        List<CtLiteral<String>> literals = parts.stream()
                .map(part -> getFactory().createLiteral(part))
                .collect(Collectors.toList());

        return getFactory().createConstructorCall(typeRef, literals.toArray(CtLiteral[]::new));
    }

    private CtExecutableReference findExecutable(Class<?> clazz, String methodName) {
        CtTypeReference<?> typeRef = getFactory().createCtTypeReference(clazz);
        return typeRef.getDeclaredExecutables()
                .stream()
                .filter(exec -> exec.getExecutableDeclaration().getSimpleName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new SpoonException(methodNotFoundError(clazz, methodName)));
    }

    private void substituteInvocation(CtInvocation<?> element, ParseResult parseResult) {
        element.setTarget(interpolatorConstructorCall(parseResult.getParts()));
        element.setExecutable(findExecutable(Interpolator.class, INTERPOLATE_METHOD_NAME));
        element.setArguments(interpolateArguments(parseResult.getExpressions()));
    }

    private String methodNotFoundError(Class<?> clazz, String methodName) {
        return String.format("Method '%s' was not found in class '%s'", methodName, clazz.getCanonicalName());
    }
}
