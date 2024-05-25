package cases.fmt.numeric_index;

import static dev.khbd.interp4j.core.Interpolations.*;

public class Main {

    public static String greet() {
        String name = "Alex";
        return fmt("Hello %1$s${name}");
    }
}
