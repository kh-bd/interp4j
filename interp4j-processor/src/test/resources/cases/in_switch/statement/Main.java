package cases.in_switch.statement;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet(String name) {
        String result = switch (name) {
            case "Alex" -> {
                System.out.println("Some statements...");
                yield s("Hello, $name");
            }
            default     -> "Who are you?";
        };
        return result;
    }

}
