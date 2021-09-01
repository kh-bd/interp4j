public class Main {

    public static void main(String[] args) {
        String name = "Alex";
        String greet = new SInterpolator("Hello ", "").interpolate(name);
        System.out.println(greet);
    }
}
