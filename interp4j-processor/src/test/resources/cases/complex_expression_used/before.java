package complex_expression_used;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
       String name = "Alex";
       String greet = s("Hello ${name.toUpperCase()}. My age is ${20 + 1}");
       System.out.println(greet);
    }
}
