package dev.khbd.interp4j.javac.plugin.s;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
class PluginUtils {

    static final Predicate<Tree> IS_S = pathPredicate("s");
    static final Predicate<Tree> IS_INTERPOLATIONS_S = pathPredicate("Interpolations.s");
    static final Predicate<Tree> IS_FQN_INTERPOLATIONS_S = pathPredicate("dev.khbd.interp4j.core.Interpolations.s");

    static Predicate<Tree> pathPredicate(String path) {
        String[] parts = path.split("\\.");
        if (parts.length == 1) {
            return identifierPredicate(parts[0]);
        }
        return memberSelectPredicate(parts);
    }

    private static Predicate<Tree> identifierPredicate(String name) {
        return tree -> {
            if (tree.getKind() != Tree.Kind.IDENTIFIER) {
                return false;
            }
            IdentifierTree identifier = (IdentifierTree) tree;
            return identifier.getName().contentEquals(name);
        };
    }

    private static Predicate<Tree> memberSelectPredicate(String[] parts) {
        Predicate<Tree> result = identifierPredicate(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result = memberSelectPredicate(result, parts[i]);
        }
        return result;
    }

    private static Predicate<Tree> memberSelectPredicate(Predicate<Tree> expressionPredicate, String name) {
        return tree -> {
            if (tree.getKind() != Tree.Kind.MEMBER_SELECT) {
                return false;
            }
            MemberSelectTree memberSelect = (MemberSelectTree) tree;
            return expressionPredicate.test(memberSelect.getExpression())
                    && memberSelect.getIdentifier().contentEquals(name);
        };
    }

    /**
     * Check if tree is `s` method call.
     *
     * @param tree    tree expression
     * @param imports declared imports
     * @return {@literal true} if supplied expression is `s` method call and {@literal false}
     * otherwise
     */
    static boolean isSMethodInvocation(ExpressionTree tree, Imports imports) {
        if (tree.getKind() != Tree.Kind.METHOD_INVOCATION) {
            return false;
        }
        MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
        if (!isSMethodInvocation(methodInvocation, imports)) {
            return false;
        }

        List<? extends ExpressionTree> arguments = methodInvocation.getArguments();
        // otherwise, it's a compile-time error
        return arguments.size() == 1;
    }

    private boolean isSMethodInvocation(MethodInvocationTree methodInvocation, Imports imports) {
        ExpressionTree methodSelect = methodInvocation.getMethodSelect();

        // s() method call
        if (IS_S.test(methodSelect)) {
            return imports.isSimpleMethodCallAllowed();
        }

        // Interpolations.s() method call
        if (IS_INTERPOLATIONS_S.test(methodSelect)) {
            return imports.isQualifiedMethodCallAllowed();
        }

        // FQN.s() method call
        return IS_FQN_INTERPOLATIONS_S.test(methodSelect);
    }
}
