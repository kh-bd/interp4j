import static dev.khbd.interp4j.core.Interpolations.s;

class Main {
    public static void main(String... args) {
        String expr = "${name}";
        String greet = s(expr);
    }
}