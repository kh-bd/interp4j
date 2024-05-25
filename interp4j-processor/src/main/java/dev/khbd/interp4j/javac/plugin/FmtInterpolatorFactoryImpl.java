package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import dev.khbd.interp4j.javac.plugin.fmt.FormatCode;
import dev.khbd.interp4j.javac.plugin.fmt.FormatExpression;
import dev.khbd.interp4j.javac.plugin.fmt.FormatExpressionParser;
import dev.khbd.interp4j.javac.plugin.fmt.FormatExpressionPart;
import dev.khbd.interp4j.javac.plugin.fmt.FormatExpressionVisitor;
import dev.khbd.interp4j.javac.plugin.fmt.FormatSpecifier;
import dev.khbd.interp4j.javac.plugin.fmt.FormatText;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sergei Khadanovich
 */
class FmtInterpolatorFactoryImpl extends AbstractInterpolatorFactory {

    FmtInterpolatorFactoryImpl() {
        super(Interpolation.FMT);
    }

    @Override
    public Interpolator create(Context context, CompilationUnitTree unit) {
        return new FmtInterpolator(context, unit);
    }

    private class FmtInterpolator extends AbstractInterpolator<FormatExpression> {

        private final TreeMaker treeMaker;
        private final ParserFactory parserFactory;
        private final Names names;

        FmtInterpolator(Context context, CompilationUnitTree unit) {
            super(unit);

            this.treeMaker = TreeMaker.instance(context);
            this.parserFactory = ParserFactory.instance(context);
            this.names = Names.instance(context);
        }

        @Override
        protected FormatExpression parse(String literal) {
            return FormatExpressionParser.getInstance().parse(literal).orElse(null);
        }

        @Override
        protected List<Message> validate(JCTree.JCLiteral literal, FormatExpression expression) {
            FormatValidator validator = new FormatValidator(literal);
            expression.visit(validator);
            return validator.getMessages();
        }

        @Override
        protected JCTree.JCExpression interpolate(JCTree.JCMethodInvocation invocation,
                                                  String literal, FormatExpression expression) {
            FormatArgumentsCollector collector = new FormatArgumentsCollector(invocation.pos);
            expression.visit(collector);

            if (!expression.hasAnyCode() && !expression.hasAnySpecifier()) {
                return treeMaker.at(invocation.pos).Literal(collector.getTemplate());
            }

            return treeMaker.at(invocation.pos)
                    .Apply(com.sun.tools.javac.util.List.nil(),
                            treeMaker.Select(treeMaker.Ident(names.fromString("String")), names.fromString("format")),
                            com.sun.tools.javac.util.List.<JCTree.JCExpression>nil()
                                    .append(treeMaker.Literal(collector.getTemplate()))
                                    .appendList(collector.arguments)
                    );
        }

        @RequiredArgsConstructor
        private final class FormatArgumentsCollector implements FormatExpressionVisitor {

            private final int basePosition;
            private final StringBuilder template = new StringBuilder();
            private com.sun.tools.javac.util.List<JCTree.JCExpression> arguments = com.sun.tools.javac.util.List.nil();

            @Override
            public void visitCodePart(FormatCode code) {
                JavacParser parser = parserFactory.newParser(code.getExpression(), false, false, false);
                JCTree.JCExpression expr = parser.parseExpression();
                expr.pos = basePosition;
                arguments = arguments.append(expr);
            }

            @Override
            public void visitTextPart(FormatText text) {
                template.append(text.getText());
            }

            @Override
            public void visitSpecifierPart(FormatSpecifier specifier) {
                template.append(specifier);
            }

            public String getTemplate() {
                return template.toString();
            }
        }

        @RequiredArgsConstructor
        private final class FormatValidator implements FormatExpressionVisitor {

            @Getter
            private final List<Message> messages = new ArrayList<>();
            private final JCTree.JCExpression literal;

            private FormatExpressionPart prev;

            @Override
            public void visitTextPart(FormatText text) {
                if (Objects.nonNull(prev)) {
                    if (prev.isSpecifier()) {
                        // text after specifier is wrong
                        FormatSpecifier specifier = (FormatSpecifier) prev;
                        if (!specifier.isPercent() && !specifier.isNextLine()) {
                            messages.add(new Message("fmt.specifier.without.expression", literal, prev.getPosition().getStart()));
                        }
                    }
                }
                this.prev = text;
            }

            @Override
            public void visitCodePart(FormatCode code) {
                if (Objects.isNull(prev)) {
                    // code part is first in expression. wrong
                    messages.add(new Message("fmt.expression.without.specifier", literal, code.getPosition().getStart()));
                } else {
                    if (!prev.isSpecifier()) {
                        // specifier must be present before each expression
                        messages.add(new Message("fmt.expression.without.specifier", literal, code.getPosition().getStart()));
                    } else {
                        // specifier present
                        // but, %% and %n are not allowed before expression
                        FormatSpecifier specifier = (FormatSpecifier) prev;
                        if (specifier.isPercent() || specifier.isNextLine()) {
                            messages.add(new Message("fmt.expression.after.special.specifiers", literal, code.getPosition().getStart()));
                        }
                    }
                }
                this.prev = code;
            }

            @Override
            public void visitSpecifierPart(FormatSpecifier specifier) {
                if (Objects.nonNull(specifier.getIndex())) {
                    messages.add(new Message("fmt.indexing", literal, specifier.getPosition().getStart()));
                }
                this.prev = specifier;
            }

            @Override
            public void finish() {
                if (prev.isSpecifier()) {
                    // specifier is last part, and it is not special specifier type
                    FormatSpecifier specifier = (FormatSpecifier) prev;
                    if (!specifier.isPercent() && !specifier.isNextLine()) {
                        messages.add(new Message("fmt.specifier.without.expression", literal, prev.getPosition().getStart()));
                    }
                }
            }
        }
    }
}
