package dev.khbd.interp4j.javac.plugin.s;

/**
 * Expression part model.
 *
 * @author Sergei_Khadanovich
 */
public interface SExpressionPart {

    /**
     * Get part kind.
     *
     * @return part kind
     */
    SExpressionPartKind kind();

    /**
     * Visit part with supplied visitor.
     *
     * @param visitor visitor
     */
    void visit(SExpressionVisitor visitor);

    /**
     * Is expression part text or not.
     *
     * @return {@literal true} if expression part is text and {@literal false} otherwise
     */
    default boolean isText() {
        return kind() == SExpressionPartKind.TEXT;
    }

    /**
     * Is expression part code or not.
     *
     * @return {@literal true} if expression part is expression and {@literal false} otherwise
     */
    default boolean isCode() {
        return kind() == SExpressionPartKind.CODE;
    }
}
