package com.epam.education;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * @author Andrei_Yakushin
 * @since 8/3/2016 10:28 AM
 */
public class Task1 {
    public static void main(String[] args) {
        //FIRST PART ---------------------------------------------------------------------------------------------------
        stream(new String[]{"A", "B", "C", "D"})
                .map(s -> new Thread(() -> {
                    System.out.println(s);
                }))
                .peek(Thread::start)
                .collect(Collectors.toList())
                .forEach(thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        //SECOND PART --------------------------------------------------------------------------------------------------
        Person[] persons = {
                new Person("E", 10),
                new Person("B", 30),
                new Person("B", 22),
                new Person("FF", 15),
                new Person("C", 15),
                new Person("D", 20),
                new Person("A", 35),
        };

        Comparator<Person> byName = (one, another) -> one.getName().compareTo(another.getName());
        Comparator<Person> byAge = (one, another) -> Integer.compare(one.getAge(), another.getAge());

        System.out.println("\n== By name ==");
        out(stream(persons).sorted(byName));
        System.out.println("\n== By age ==");
        out(stream(persons).sorted(byAge));
        System.out.println("\n== By name and age ==");
        out(stream(persons).sorted(byName.thenComparing(byAge)));
        System.out.println("\n== By age and name ==");
        out(stream(persons).sorted(byAge.thenComparing(byName)));

        System.out.println("\n== Teenagers (Predicate) ==");
        out(stream(persons).filter(person -> person.getAge() < 20));

        System.out.println("\n== Reduce to one string (BiFunction and Function) ==");
        System.out.println(stream(persons).parallel().reduce("", (s, person) -> s + ", " + person.toString(), (a, b) -> a + b));

        System.out.println("\n== Names (Consumer) ==");
        stream(persons).forEach(person -> System.out.println(person.getName()));

        System.out.println("\n== Grouped by age (Supplier and BiConsumer) ==");
        Map<Integer, List<String>> map = stream(persons).collect(
                () -> new HashMap<Integer, List<String>>(),
                (ageGroup, person) -> ageGroup.computeIfAbsent(person.getAge(), (age) -> new ArrayList<>()).add(person.getName()),
                (a, b) -> Stream.of(a, b)
                        .flatMap(m -> m.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        System.out.println(map);

        System.out.println("\n== Magic number from persons (Default and static methods) ==");
        stream(persons).mapToInt(Formula.age().add(Formula.nameLength())).forEach(System.out::println);
    }

    private static void out(Stream<Person> persons) {
        persons.map(Person::toString).forEach(System.out::println);
    }

    //------------------------------------------------------------------------------------------------------------------

    private static class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "{'" + name + '\'' + ", " + age + '}';
        }
    }

    private interface Formula extends ToIntFunction<Person> {
        default Formula add(Formula that) {
            return person -> this.applyAsInt(person) + that.applyAsInt(person);
        }

        default Formula subtract(Formula that) {
            return person -> this.applyAsInt(person) - that.applyAsInt(person);
        }

        static Formula age() {
            return Person::getAge;
        }

        static Formula nameLength() {
            return new NameLengthFormula();
        }

        static Formula magicNumber() {
            return new Formula() {
                @Override
                public int applyAsInt(Person person) {
                    return person.hashCode();
                }
            };
        }
    }

    private static class NameLengthFormula implements Formula {
        @Override
        public int applyAsInt(Person person) {
            return person.getName().length();
        }
    }
}
