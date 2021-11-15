package without_expressions_inside;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
       System.out.println(new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello Alex").interpolate());
    }
}
