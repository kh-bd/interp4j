package com.github.interp4j.examples;

import static com.github.interp4j.core.Interpolations.s;

/**
 * @author Sergei_Khadanovich
 */
public class Main {

    public static void main(String[] args) {
        String name = args.length > 0 ? args[0] : "Alex";
        String greet = s("Hello ${name}. Who are you?");
        System.out.println(greet);
    }
}
