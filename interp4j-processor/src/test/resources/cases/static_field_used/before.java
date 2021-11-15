package static_field_used;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {

    private static final String NAME = "Alex";

    public static void main(String... args) {
       String greet = s("Hello ${NAME}");
       System.out.println(greet);
    }
}
