package com.matt.blog;

import java.util.function.Function;

public class SomeFunction implements Function<Integer, String> {

    @Override
    public String apply(Integer integer) {


        return String.valueOf(integer / 2);
    }
}
