package cases.fmt.specifier_at_start_no_code;

import static dev.khbd.interp4j.core.Interpolations.*;

public class Main {

    public static String greet() {
        String name = "Alex";
        return fmt("%s");
    }
}
