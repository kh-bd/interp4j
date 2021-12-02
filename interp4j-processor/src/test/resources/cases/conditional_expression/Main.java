package cases.conditional_expression;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet(boolean flag) {
        String name = "Alex";
        return flag ? s("Hello, ${name}") : s("Hi, ${name}");
    }
}
