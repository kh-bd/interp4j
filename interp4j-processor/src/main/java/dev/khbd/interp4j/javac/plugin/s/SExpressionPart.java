package dev.khbd.interp4j.javac.plugin.s;

/**
 * Expression part model.
 *
 * @author Sergei_Khadanovich
 */
public sealed interface SExpressionPart permits SText, SCode {

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
}
