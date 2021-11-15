package assignment_import_static;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
       String name = "Alex";
       String greet = s("Hello ${name}");
       System.out.println(greet);
    }
}
