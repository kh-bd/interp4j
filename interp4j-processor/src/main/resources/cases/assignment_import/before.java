package assignment_import;

import dev.khbd.interp4j.core.Interpolations;

class Main {
    public static void main(String... args) {
       String name = "Alex";
       String greet = Interpolations.s("Hello ${name}");
       System.out.println(greet);
    }
}
