package cases.lambda.expression;

import java.util.function.Function;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet() {
        Function<String, String> f = (name) -> s("Hello, $name");
        return f.apply("Alex");
    }
}
