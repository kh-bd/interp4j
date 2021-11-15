package method_arguments;

import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        print(s("Hello ${name}"), s("How are you, ${name}"));
    }

    public static void print(Object... objects) {
        for (Object object : objects) {
            System.out.println(object);
        }
    }
}
