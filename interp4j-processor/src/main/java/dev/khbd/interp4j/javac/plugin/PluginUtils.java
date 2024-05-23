package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import lombok.experimental.UtilityClass;

import java.util.function.Predicate;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class PluginUtils {

    public static Predicate<Tree> pathPredicate(String... path) {
        return pathPredicate(String.join(".", path));
    }

    public static Predicate<Tree> pathPredicate(String path) {
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

}
