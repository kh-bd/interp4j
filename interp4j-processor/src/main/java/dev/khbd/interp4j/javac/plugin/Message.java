package dev.khbd.interp4j.javac.plugin;

import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.JCDiagnostic;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

/**
 * Message data type.
 *
 * @author Sergei Khadanovich
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {

    String code;
    Position position;

    Message(String code, JCTree.JCExpression expression, int shift) {
        this(code, new Position(expression, shift));
    }

    Message(String code, JCTree.JCExpression expression) {
        this(code, new Position(expression, 0));
    }

    /**
     * Diagnostic position impl.
     */
    @ToString
    @EqualsAndHashCode
    static class Position implements JCDiagnostic.DiagnosticPosition {

        private final int start;

        Position(JCTree.JCExpression expression, int shift) {
            this.start = expression.getStartPosition() + shift;
        }

        @Override
        public int getEndPosition(EndPosTable endPosTable) {
            return start;
        }

        @Override
        public int getPreferredPosition() {
            return start;
        }

        @Override
        public int getStartPosition() {
            return start;
        }

        @Override
        public JCTree getTree() {
            return null;
        }
    }
}
