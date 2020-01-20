package com.matt.blog;


import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import io.vavr.control.Either;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Test;

public class CollectorUtilsTest {

    @Test
    public void toMultiMap_MultipleValues_groupedByKey() {

        Map<String, List<String>> grouped =
                Stream.of("Apple", "Aardvark", "Amy", "Danny Dyer", "Washer Dryer", "Dave")
                        .collect(CollectorUtils.toMultimap(s -> s.substring(0, 1), s -> s));

        assertThat(grouped)
                .isEqualTo(
                        ImmutableMap.of(
                                "A",
                                Arrays.asList("Apple", "Aardvark", "Amy"),
                                "D",
                                Arrays.asList("Danny Dyer", "Dave"),
                                "W",
                                singletonList("Washer Dryer")));
    }

    @Test
    public void toMultiMap_SingleValue_groupedByKey() {

        Map<String, List<String>> grouped =
                Stream.of("Aarman Tamzarian")
                        .collect(CollectorUtils.toMultimap(s -> s.substring(0, 1), s -> s));

        assertThat(grouped)
                .isEqualTo(
                        ImmutableMap.of(
                                "A",
                                singletonList("Aarman Tamzarian")));
    }

    @Test
    public void toEitherList_ManyValues_GroupsByType() {
        Stream<Either<Integer, String>> left = Stream.of(
                Either.left(1),
                Either.left(2),
                Either.right("A"),
                Either.left(3),
                Either.right("B"));

        var result = left.collect(CollectorUtils.toEitherList());

        assertThat(result._1())
                .containsExactly(1, 2, 3);
        assertThat(result._2())
                .containsExactly("A", "B");

    }
}