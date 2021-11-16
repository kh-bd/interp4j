# Interp4j is a string interpolation library for Java

## Why we need it?

*** 

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

Better, but we can do even better. String interpolation can help us
to write such code in a nicer and shorter way.

```java
class Greeter {
    public String describe(Person person) {
        return s("Hello! My name is ${person.getName()}. I'm ${person.getAge()}");
    }
}
```