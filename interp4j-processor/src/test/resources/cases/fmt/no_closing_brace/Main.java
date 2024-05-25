package cases.fmt.no_closing_brace;

import static dev.khbd.interp4j.core.Interpolations.*;

public class Main {

    public static String greet() {
        String name = "Alex";
        return fmt("Hello %s${name");
    }
}
