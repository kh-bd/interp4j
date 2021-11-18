package method_inside_expr;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        print(s("Hello ${upper(name)}"));
        print(s("Hello ${upper(\"name\")}"));
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
