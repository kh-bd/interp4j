# Interp4j is a string interpolation library for Java

## Why we need it?

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
    <version>LATEST</version>
</dependency>
```

Now, you can use `Interpolations.s` function in your code. This function is entry point
to interpolations.

Second, you need to configure your build tool to run interpolation process
before source files compilation.

## Maven support

To interpolate strings in maven-based projects use
[interp4j-maven-plugin](https://github.com/KhadanovichSergey/interp4j-maven-plugin).


## Gradle support

in progress

## Intellij support

To support `interp4j` by intellij, install
[interp4j-intellij-plugin](https://github.com/KhadanovichSergey/interp4j-intellij-plugin).