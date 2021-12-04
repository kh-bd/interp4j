package cases.member_ref;

import static dev.khbd.interp4j.core.Interpolations.s;

import java.util.function.Supplier;

public class Main {

    public static String greet(String name) {
        return get(s("Hello, $name")::toUpperCase);
    }

    private static <T> T get(Supplier<? extends T> f) {
        return f.get();
    }
}
