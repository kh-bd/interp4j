package dev.khbd.interp4j.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import static dev.khbd.interp4j.core.Interpolations.s;

/**
 * @author Sergei_Khadanovich
 */
@Fork(value = 1, warmups = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InterpolationBenchmark {

    @Benchmark
    public void manual(Blackhole hole, Person person) {
        hole.consume("Hello! I am " + person.name + ". I am " + person.age);
    }

    @Benchmark
    public void s_interpolation(Blackhole hole, Person person) {
        hole.consume(s("Hello! I am ${person.name}. I am ${person.age}"));
    }

    @Benchmark
    public void string_format(Blackhole hole, Person person) {
        hole.consume(String.format("Hello! I am %s. I am %d", person.name, person.age));
    }

    @State(Scope.Benchmark)
    public static class Person {
        public String name;
        public int age;

        @Setup(Level.Trial)
        public void setUp() {
            this.name = "Alex";
            this.age = 21;
        }
    }
}
