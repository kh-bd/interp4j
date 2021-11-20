package assignment_import;

import java.util.List;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        String greet = new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello ", "").interpolate(name);
        printList(List.of(greet, name));
    }

    static void printList(List<String> list) {
    }

}
