package dev.khbd.interp4j.javac.plugin.fmt;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Format specifier in format expression.
 *
 * <p>Note: Only conversion is required by grammar. Any other part can be absent.
 *
 * @author Sergei_Khadanovich
 */
@Value
@AllArgsConstructor
public class FormatSpecifier implements FormatExpressionPart {

    Index index;
    String flags;
    Integer width;
    Integer precision;
    Conversion conversion;
    Position position;

    FormatSpecifier(Conversion conversion, Position position) {
        this(null, null, null, null, conversion, position);
    }

    FormatSpecifier(Index index, Conversion conversion, Position position) {
        this(index, null, null, null, conversion, position);
    }

    @Override
    public FormatExpressionPartKind kind() {
        return FormatExpressionPartKind.SPECIFIER;
    }

    @Override
    public void visit(FormatExpressionVisitor visitor) {
        visitor.visitSpecifierPart(this);
    }
}
