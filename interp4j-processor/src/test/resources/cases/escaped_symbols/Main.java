package cases.escaped_symbols;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        String name = "Alex";
        return s("Hello, \"\"$name\"\"");
    }
}
