package dev.khbd.interp4j.javac.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sergei_Khadanovich
 */
public abstract class AbstractPluginTest {

    protected final TestCompiler compiler = new TestCompiler();

    private static class TestSourceFile extends SimpleJavaFileObject {

        TestSourceFile(URI uri) {
            super(uri, Kind.SOURCE);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            Path path = Paths.get(toUri());
            return Files.readString(path);
        }
    }

    private static class InMemoryTestSourceFile extends SimpleJavaFileObject {

        private final String source;

        InMemoryTestSourceFile(String path, String source) {
            super(URI.create("string://" + path), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    private static class InMemoryClassFile extends SimpleJavaFileObject {

        @Getter
        private final String className;
        private ByteArrayOutputStream out;

        InMemoryClassFile(URI uri, String className) {
            super(uri, Kind.CLASS);
            this.className = className;
        }

        @Override
        public OutputStream openOutputStream() {
            return out = new ByteArrayOutputStream();
        }

        public byte[] getCompiledBinaries() {
            return out.toByteArray();
        }
    }

    private static class TestFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private final List<InMemoryClassFile> compiled = new ArrayList<>();

        TestFileManager(StandardJavaFileManager manager) {
            super(manager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                                                   String className,
                                                   JavaFileObject.Kind kind,
                                                   FileObject sibling) {
            InMemoryClassFile result = new InMemoryClassFile(
                    URI.create("string://" + className),
                    className
            );
            compiled.add(result);
            return result;
        }

        public List<InMemoryClassFile> getCompiled() {
            return compiled;
        }
    }

    @RequiredArgsConstructor
    private static class TestClassLoader extends ClassLoader {

        private final List<InMemoryClassFile> classFiles;

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            InMemoryClassFile classFile = findInMemoryClassFile(name);
            byte[] bytes = classFile.getCompiledBinaries();
            return defineClass(name, bytes, 0, bytes.length);
        }

        private InMemoryClassFile findInMemoryClassFile(String name) throws ClassNotFoundException {
            return classFiles.stream()
                    .filter(file -> file.getClassName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new ClassNotFoundException(name));
        }
    }

    private static class TestDiagnosticListener implements javax.tools.DiagnosticListener<JavaFileObject> {

        @Getter
        private final List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            diagnostics.add(diagnostic);
        }
    }

    protected static class TestCompiler {

        public CompilationResult compile(String path, String source) {
            return compile(new PluginOptions(true), path, source);
        }

        public CompilationResult compile(PluginOptions options, String path, String source) {
            InMemoryTestSourceFile toCompile = new InMemoryTestSourceFile(path, source);
            return compile(options, List.of(toCompile));
        }

        public CompilationResult compile(PluginOptions options, String... paths) {
            List<TestSourceFile> toCompile = Stream.of(paths)
                    .map(this::toUri)
                    .map(TestSourceFile::new)
                    .collect(Collectors.toList());

            return compile(options, toCompile);
        }

        public CompilationResult compile(PluginOptions options, List<? extends JavaFileObject> toCompile) {
            TestDiagnosticListener diagnostic = new TestDiagnosticListener();

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            TestFileManager fileManager = new TestFileManager(
                    compiler.getStandardFileManager(diagnostic, null, null));

            List<String> arguments = new ArrayList<>();
            arguments.add("-classpath");
            arguments.add(System.getProperty("java.class.path"));
            arguments.add(options.toString());
            arguments.add("--release=21");
            arguments.add("--enable-preview");

            JavaCompiler.CompilationTask task
                    = compiler.getTask(new StringWriter(), fileManager, diagnostic, arguments, null,
                    toCompile);

            task.call();

            return new CompilationResult(new TestClassLoader(fileManager.getCompiled()), diagnostic.getDiagnostics());
        }

        private URI toUri(String path) {
            try {
                return this.getClass().getResource(path).toURI();
            } catch (URISyntaxException se) {
                throw new RuntimeException(se);
            }
        }
    }

    protected record PluginOptions(boolean prettyPrint) {

        @Override
        public String toString() {
            return "-Xplugin:interp4j prettyPrint.after.interpolation=" + prettyPrint;
        }
    }

    @Value
    protected static class CompilationResult {
        ClassLoader classLoader;
        List<Diagnostic<? extends JavaFileObject>> diagnostics;

        public boolean isSuccess() {
            return diagnostics.stream()
                    .noneMatch(d -> d.getKind() == Diagnostic.Kind.ERROR);
        }

        public boolean isFail() {
            return !isSuccess();
        }

        public List<Diagnostic<? extends JavaFileObject>> getErrors() {
            return diagnostics.stream()
                    .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                    .collect(Collectors.toList());
        }
    }
}
