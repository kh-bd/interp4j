package non_literal_string_used;

import dev.khbd.interp4j.core.Interpolations;

class Main {

    private static final String EXPR = "Hello ${name}";

    public static void main(String... args) {
       String name = "Alex";
       String greet = Interpolations.s(EXPR);
       System.out.println(greet);
    }
}
