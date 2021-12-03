package cases.method_invocation.at_receiver_position;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        return s("  Hello, $name  ").trim().replace("Hello", "Hi").toUpperCase();
    }
}
