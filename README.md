# Interp4j is a string interpolation library for Java

[![CI latest](https://github.com/kh-bd/interp4j/actions/workflows/main-tests.yml/badge.svg)](https://github.com/kh-bd/interp4j/actions/workflows/main-tests.yml)

## Why do we need it?

To understand why we need string interpolation and how to use it, consider following example:
We have to implement a function, which can build a short person descriptor.

StringBuilder implementation:

```java
class Greeter {
    public String describe(Person person) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello! My name is ");
        builder.append(person.getName());
        builder.append(". I'm ");
        builder.append(person.getAge());
        return builder.toString();
    }
}
```

A lot of code to implement such simple thing. May be `String.format` can help us.

```java
class Greeter {
    public String describe(Person person) {
        String template = "Hello! My name is %s. I'm %d";
        return String.format(template, person.getName(), person.getAge());
    }
}

```

Better, but we can do even better. String interpolation can help us to write such code in a nicer and shorter way.

```java
class Greeter {
    public String describe(Person person) {
        return s("Hello! My name is ${person.getName()}. I'm ${person.getAge()}");
    }
}
```

## How to use it?

First, you need to add `interp4j-core` dependency to your project. For example, if you use maven, add the following
configuration to your `pom.xml` file:

```xml

<dependency>
    <groupId>dev.khbd.interp4j</groupId>
    <artifactId>interp4j-core</artifactId>
    <scope>compile</scope>
    <version>LATEST</version>
</dependency>
```

Now, you can use function from `Interpolations` class in your code. These function are entry point to interpolations.

Second, you need to configure your build tool to run interpolation process before source files compilation.

### `s` interpolator

`s` interpolator is simple. Write down a text and inject any java expression into in `${...}`. If an expression is
a java identifier curly braces can be omitted. For example, `s("Hello, ${person.name}")` or `s("Hello, $name")`.

### `fmt` interpolator

`fmt` interpolator is a replacement for `String.format`. The usage is quite the same as `s` interpolator, but
before each expression valid specifier must be provided. For example, `fmt("Hello, %20.4s${person.name}")`.
All features of `String.format` are supported, but there are several syntactic rules:

- before each expression part a valid specifier must be present
- numeric or implicit indexing are not supported. So `fmt("%1$s${person.name}")` or `fmt("%1$s${person.name}, %<d${person.age}")` are not valid.
- after specifiers `$$` and `%n` no code is allowed

## Versions

We are going to support separate version for each LTS release as long as that release is supported.
In the following table, you can find the latest interp4j version for each supported java version.

| Java<br/> version | Latest release                                                                                                                                                                                           |
|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `1.8`             | [![Maven jdk1.8](https://img.shields.io/maven-central/v/dev.khbd.interp4j/interp4j?color=brightgreen&versionSuffix=_jre1.8)](https://mvnrepository.com/artifact/dev.khbd.interp4j/interp4j/2.0.0_jre1.8) |
| `11`              | [![Maven jdk11](https://img.shields.io/maven-central/v/dev.khbd.interp4j/interp4j?color=brightgreen&versionSuffix=_jre11)](https://mvnrepository.com/artifact/dev.khbd.interp4j/interp4j/2.0.0_jre11)    |
| `17`              | [![Maven jdk17](https://img.shields.io/maven-central/v/dev.khbd.interp4j/interp4j?color=brightgreen&versionSuffix=_jre17)](https://mvnrepository.com/artifact/dev.khbd.interp4j/interp4j/2.0.0_jre17)    |
| `21`              | [![Maven jdk21](https://img.shields.io/maven-central/v/dev.khbd.interp4j/interp4j?color=brightgreen&versionSuffix=_jre21)](https://mvnrepository.com/artifact/dev.khbd.interp4j/interp4j/2.0.0_jre21)    |

## Maven support

To interpolate strings in maven-based projects you have to configure compiler to enable interp4j compiler plugin during
compilation. Add the following configuration to your `pom.xml` file and that's it.

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <!-- enable interp4j compiler plugin -->
            <arg>-Xplugin:interp4j</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>dev.khbd.interp4j</groupId>
                <artifactId>interp4j-processor</artifactId>
                <version>LATEST</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Compiler plugin uses internal jdk api to interpolate string literals and
this api is [strongly encapsulated by default](https://openjdk.org/jeps/403) in jdk 17.
To relax it at compile time configuration should be changed accordingly.

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <fork>true</fork>
        <compilerArgs>
            <!-- enable interp4j compiler plugin -->
            <arg>-Xplugin:interp4j</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>dev.khbd.interp4j</groupId>
                <artifactId>interp4j-processor</artifactId>
                <version>LATEST</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Additional exports are needed only for compiling process, resulted code will not be dependent on internal jdk api.

## Gradle support

To interpolate strings in gradle-based projects you have to configure compiler to enable interp4j compiler plugin during
compilation. Add the following configuration to your `build.gradle` file and that's it.

```groovy
dependencies {
    compileOnly group: 'dev.khbd.interp4j', name: 'interp4j-core', version: interp4j_version
    annotationProcessor group: 'dev.khbd.interp4j', name: 'interp4j-processor', version: interp4j_version
}

task.withType(JavaCompile) {
    options.fork = true
    options.compilerArgs.add('-Xplugin:interp4j')
}
```

If you use jdk 17 or higher additional options are required to allow compiler plugin
to use internal jdk api.

```groovy
dependencies {
    compileOnly group: 'dev.khbd.interp4j', name: 'interp4j-core', version: interp4j_version
    annotationProcessor group: 'dev.khbd.interp4j', name: 'interp4j-processor', version: interp4j_version
}

task.withType(JavaCompile) {
    options.fork = true
    options.compilerArgs.add('-Xplugin:interp4j')
    options.forkOptions.jvmArgs.addAll([
            '--add-exports', 'jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED',
            '--add-exports', 'jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED',
            '--add-exports', 'jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED',
            '--add-exports', 'jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED'
    ])
}
```

Additional exports are needed only for compiling process, resulted code will not be dependent on internal jdk api.

## Intellij support

To support `interp4j` by intellij, install
[interp4j-intellij-plugin](https://github.com/kh-bd/interp4j-intellij-plugin).

## How to look at modified source code?

To look at modified source code after interpolation, set flag `prettyPrint.after.interpolation` to `true`. For example,
for maven-based projects it can look like that:

```xml

<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-Xplugin:interp4j prettyPrint.after.interpolation=true</arg>
        </compilerArgs>
        ...
    </configuration>
</plugin>
```

By default, this feature is disabled.

## Benchmarks

All benchmarks were run on:

- Machine: MacBook Pro 2023
- Processor: Apple M2 Max
- Memory: 32 GB LPDDR5

See latest benchmark
result [here](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/kh-bd/interp4j/main/readme/beanchmark/jmh_v1.1.0_j21.json)
.

As you can see, compile time interpolation is about 10 times faster then `String.format`
and at the same time as fast as manual string concatenation. Benchmarks source code can
be found in `interp4j-benchmark` module.

### Run benchmarks on your own machine

To run benchmarks do several steps:

- pull project to your machine
- run from root directory `mvn package -Pbenchmark`
- go to `interp4j-benchmark/target` directory. `interp4j-benchmark-${version}-jar-with-dependencies.jar` should be
  generated
- run
  command `java -cp ./interp4j-benchmark-${version}-jar-with-dependencies.jar dev.khbd.interp4j.benchmark.BenchmarkRunner -rf json`
- `jmh-result.json` report should be generated
- view it through [jmh visualizer](https://jmh.morethan.io/)
