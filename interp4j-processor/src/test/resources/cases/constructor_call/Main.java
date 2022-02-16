package cases.constructor_call;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        return new Greeter(s("Hello, $name")).greet();
    }

    private static class Greeter {
        final String name;

        Greeter(String name) {
            this.name = name;
        }

        String greet() {
            return name;
        }
    }
}
