package assignment_fqn_access;

class Main {
    public static void main(String... args) {
       String name = "Alex";
       String greet = dev.khbd.interp4j.core.Interpolations.s("Hello ${name}");
       System.out.println(greet);
    }
}
