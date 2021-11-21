package dev.khbd.interp4j.processor.s.expr;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergei_Khadanovich
 */
@ToString
@EqualsAndHashCode
public class SExpression implements Iterable<SExpressionPart> {

    private final List<SExpressionPart> parts;

    SExpression() {
        this.parts = new ArrayList<>();
    }

    SExpression addPart(SExpressionPart part) {
        this.parts.add(part);
        return this;
    }

    /**
     * Visit s expression.
     *
     * @param visitor visitor
     */
    public void visit(SExpressionVisitor visitor) {
        visitor.start();
        for (SExpressionPart part : parts) {
            part.visit(visitor);
        }
        visitor.finish();
    }

    @Override
    public Iterator<SExpressionPart> iterator() {
        // Don't use ArrayList#iterator, because underling implementation supports
        // Iterator#remove, but our structure is immutable
        Iterator<SExpressionPart> iterator = parts.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public SExpressionPart next() {
                return iterator.next();
            }
        };
    }

    public List<SExpressionPart> getParts() {
        return new ArrayList<>(parts);
    }

    public boolean hasAnyExpression() {
        return parts.stream().anyMatch(SExpressionPart::isExpression);
    }
}
