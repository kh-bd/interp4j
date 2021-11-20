package method_arguments;

class Main {
    public static void main(String... args) {
        String name = "Alex";
        print(new dev.khbd.interp4j.core.internal.s.SInterpolator("Hello ", "").interpolate(name),
                new dev.khbd.interp4j.core.internal.s.SInterpolator("How are you, ", "").interpolate(name));
    }

    public static void print(Object... objects) {
        for (Object object : objects) {
            System.out.println(object);
        }
    }
}
