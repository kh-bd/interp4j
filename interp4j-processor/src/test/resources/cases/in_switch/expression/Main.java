package cases.in_switch.expression;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet(String name) {
        String result = switch (name) {
            case "Alex" -> s("Hello, $name");
            default -> "Who are you?";
        };
        return result;
    }

}
