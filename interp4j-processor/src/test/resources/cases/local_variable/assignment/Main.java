package cases.local_variable.assignment;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        String greet = null;
        greet = s("Hello, ${name}");
        return greet;
    }
}
