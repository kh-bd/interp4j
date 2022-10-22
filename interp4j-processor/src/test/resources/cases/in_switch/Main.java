package cases.in_switch;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet(boolean isBos, String name) {
        String result;

        // really weird case to support s function here, but maybe someone will need it
        switch (s("${chooseGreet(isBos)}, $name")) {
            case "Hello, Alex":
                result = "Alex was greeted correctly";
                break;
            case "Hi, Alex":
                result = "Alex was greeted incorrectly";
                break;
            default:
                throw new RuntimeException("Error");
        }

        return result;
    }

    private static String chooseGreet(boolean isBos) {
        return isBos ? "Hello" : "Hi";
    }

    public static String greetExpression(String name) {
        String result = switch (name) {
            case "Alex" -> s("Hello, $name");
            default     -> "Who are you?";
        };
        return result;
    }

    public static String greetYield(String name) {
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
