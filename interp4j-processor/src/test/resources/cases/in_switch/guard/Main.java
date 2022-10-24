package cases.in_switch.guard;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    public static String greet(Object name) {
        String result = switch (name) {
            case String str && s("_${str.toUpperCase()}_").length() > 4 -> str;
            default -> "Opps!";
        };
        return result;
    }

}
