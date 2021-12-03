package cases.static_variable.declaration;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    private static String NAME = "Alex";
    public static final String GREET = s("Hello, $NAME");
}
