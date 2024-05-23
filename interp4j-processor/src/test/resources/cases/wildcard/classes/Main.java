package cases.wildcard.classes;

import dev.khbd.interp4j.core.*;

public class Main {

    public static String greet() {
        String name = "Alex";
        return Interpolations.s("I'm ${name}. ") + Interpolations.s("I'm ${name.toUpperCase()}");
    }
}