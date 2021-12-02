package cases.local_variable.wrong_expression;

import dev.khbd.interp4j.core.Interpolations;

public class Main {

    public static String greet() {
        String name = "Alex";
        String greet = Interpolations.s("Hello ${Alex");
        return greet;
    }
}
