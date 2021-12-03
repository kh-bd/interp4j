package cases.in_binary_operator;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        return s("I'm ${name}. ") + s("I'm ${name.toUpperCase()}");
    }
}
