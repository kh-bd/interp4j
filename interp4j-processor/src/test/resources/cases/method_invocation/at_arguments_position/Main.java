package cases.method_invocation.at_arguments_position;

import static dev.khbd.interp4j.core.Interpolations.s;

public class Main {

    private static final int AGE = 20;

    public static String greet(String name) {
        return concat(s("It's ${name}. "), s("$name is $AGE"));
    }

    private static String concat(String str1, String str2) {
        return str1 + str2;
    }
}
