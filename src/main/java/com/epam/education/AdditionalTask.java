package com.epam.education;

import javafx.util.Pair;

import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author Andrei_Yakushin
 * @since 8/3/2016 12:43 PM
 */
public class AdditionalTask {
    public static void main(String[] args) {
        Stream<Integer> first = Stream.iterate(-1, value -> -value);
        Stream<Integer> second = Stream.iterate(new Pair<>(1, 1), pair -> new Pair<>(pair.getValue(), pair.getKey() + pair.getValue()))
                .map(Pair::getKey);

        Iterator<Integer> iterator = second.iterator();
        Stream<Integer> c = first.map(value -> value * iterator.next());

        c.skip(9).limit(11).forEach(System.out::println);
    }
}
