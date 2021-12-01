package dev.khbd.interp4j.javac.plugin;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import dev.khbd.interp4j.core.Interpolations;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Sergei_Khadanovich
 */
public class SInterpolationPlugin implements Plugin {

    @Override
    public String getName() {
        return "interp4j";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new TaskListener() {
            @Override
            public void finished(TaskEvent event) {
                if (event.getKind() != TaskEvent.Kind.PARSE) {
                    return;
                }

                CompilationUnitTree unit = event.getCompilationUnit();
                SImports imports = resolveImports(unit);
                unit.accept(new SInterpolationTreeScanner(imports), null);
            }
        });
    }

    @RequiredArgsConstructor
    private static class SInterpolationTreeScanner extends TreeScanner<Void, Void> {

        final SImports imports;

        // todo: can be built here???
        @Override
        public Void visitImport(ImportTree importTree, Void unused) {
            super.visitImport(importTree, unused);
            return null;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree methodInvocation, Void unused) {
            super.visitMethodInvocation(methodInvocation, unused);

            if (isSMethodInvocation(methodInvocation)) {
                System.out.println("S method invocation detected!!!");
            }

            return null;
        }

        private boolean isSMethodInvocation(MethodInvocationTree methodInvocation) {
            String methodSelect = methodInvocation.getMethodSelect().toString();

            ExpressionTree expressionTree = methodInvocation.getMethodSelect();


            if ("s".equals(methodSelect)) {
                return imports.isStaticMethodImportPresent();
            }

            if ("Interpolations.s".equals(methodSelect)) {
                return imports.isClassImportPresent();
            }

            return methodSelect.equals(Interpolations.class.getCanonicalName() + ".s");
        }
    }

    private static SImports resolveImports(CompilationUnitTree unit) {
        SImports imports = new SImports();
        for (ImportTree importTree : unit.getImports()) {
            if (isInterpolationsImport(importTree)) {
                imports.add(SImport.of(importTree.isStatic()));
            }
        }
        return imports;
    }

    private static boolean isInterpolationsImport(ImportTree importTree) {
        Tree identifier = importTree.getQualifiedIdentifier();
        return identifier.toString().startsWith(Interpolations.class.getCanonicalName());
    }

    private static class SImports {

        final Set<SImport> imports = EnumSet.noneOf(SImport.class);

        void add(SImport sImport) {
            imports.add(sImport);
        }

        boolean isStaticMethodImportPresent() {
            return imports.contains(SImport.METHOD);
        }

        boolean isClassImportPresent() {
            return imports.contains(SImport.CLASS);
        }
    }

    /**
     * Kind of `s` method import.
     */
    private enum SImport {
        CLASS,
        METHOD;

        static SImport of(boolean isStatic) {
            if (isStatic) {
                return METHOD;
            }
            return CLASS;
        }
    }
}
