import static com.github.interp4j.core.Interpolations.s;

public class Main {

    public static void main(String[] args) {
        String name = "Alex";
        String greet = s("Hello ${name}");
        System.out.println(greet);
    }
}
