package cases.local_variable.compound_assignment;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        String greet = "H";
        greet += s("ello, ${name}");
        return greet;
    }
}
