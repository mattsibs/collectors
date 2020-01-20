package com.matt.blog;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class FailFastTest {


    @Test
    public void failFastStream_OddNumbers_FailsFast() {
        List<Integer> iteratedValues = new ArrayList<>();
        Stream<Integer> stream = Stream.of(1, 3, 4, 5, 6, 7, 8, 9)
                .peek(iteratedValues::add);

        Validation<String, List<Integer>> result = FailFast
                .failFast(stream, integer -> integer % 2 == 0, "Only odd values please")
                .map(integerStream -> integerStream.collect(Collectors.toList()));

        assertThat(result.isInvalid())
                .isEqualTo(true);

        assertThat(result.getError())
                .isEqualTo("Only odd values please");

        assertThat(iteratedValues).containsOnly(1, 3, 4);
    }
}
