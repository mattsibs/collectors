package com.matt.blog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamExample {

    public void test() {
        List<Integer> collect = Arrays.asList("1", "2", "3", "4")
                .stream()
                .map(s -> Integer.valueOf(s))
                .collect(Collectors.toList());
    }


    public void lazy() {
        Stream<String> stringStream = Arrays.asList("1", "2", "3", "4")
                .stream();

        System.out.println("log line 1");

        List<Integer> collect = stringStream
                .peek(s -> System.out.println("Logging " + s))
                .map(s -> Integer.valueOf(s))
                .collect(Collectors.toList());
    }

    public void state() {
        Map<String, Integer> state = new HashMap<>();

        List<Integer> collect = Arrays.asList("1", "2", "3", "4")
                .stream()
                .map(s -> {
                    state.put(s, Integer.valueOf(s));
                    return Integer.valueOf(s);
                })
                .collect(Collectors.toList());
    }

}
