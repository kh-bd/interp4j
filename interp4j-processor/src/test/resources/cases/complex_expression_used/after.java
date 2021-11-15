package complex_expression_used;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        String greet = new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello ", ". My age is ", "").interpolate(name.toUpperCase(), 20 + 1);
        System.out.println(greet);
    }
}
