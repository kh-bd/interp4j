package cases.static_variable.assignment;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static final String GREET;

    static {
        String name = "Alex";
        GREET = s("Hello, $name");
    }
}
