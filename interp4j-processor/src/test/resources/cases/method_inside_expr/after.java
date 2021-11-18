package method_inside_expr;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        print(new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello ", "").interpolate(upper(name)));
        print(new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello ", "").interpolate(upper("name")));
    }

    static String upper(String value) {
        return value.toUpperCase();
    }

    public static void print(Object... objects) {
        for (Object object : objects) {
            System.out.println(object);
        }
    }
}
