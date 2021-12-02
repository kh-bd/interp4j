package cases.local_variable.non_literal_string_used;

import dev.khbd.interp4j.core.Interpolations;

public class Main {

    private static final String EXPR = "Hello ${name}";

    public static String greet() {
        String greet = Interpolations.s(EXPR);
        return greet;
    }
}
