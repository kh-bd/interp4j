package assignment_import;

import dev.khbd.interp4j.core.Interpolations;
import java.util.List;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        String greet = Interpolations.s("Hello ${name}");
        printList(List.of(greet, name));
    }

    static void printList(List<String> list) {
    }
}
