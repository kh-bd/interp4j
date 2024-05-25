package cases.fmt.valid_with_codes;

import static dev.khbd.interp4j.core.Interpolations.*;

public class Main {

    public static String greet() {
        String name = "Alex";
        int age = 20;
        return fmt("Hello, %s${name}. Are you %d${age}?");
    }
}
