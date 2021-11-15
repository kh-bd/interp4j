package null_literal_value_used;

import dev.khbd.interp4j.core.Interpolations;

class Main {

    public static void main(String... args) {
       String name = "Alex";
       String greet = Interpolations.s(null);
       System.out.println(greet);
    }
}
