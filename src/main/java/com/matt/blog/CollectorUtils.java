package com.matt.blog;

import com.google.common.collect.ImmutableList;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public class CollectorUtils {

    public static <T, K, R> Collector<T, Map<K, List<R>>, Map<K, List<R>>> toMultimap(
            final Function<T, K> keyExtractor, final Function<T, R> valueExtractor) {

        BiConsumer<Map<K, List<R>>, T> accumulator =
                (keyListMap, t) -> {
                    K key = keyExtractor.apply(t);
                    keyListMap.putIfAbsent(key, new ArrayList<>());
                    keyListMap.get(key).add(valueExtractor.apply(t));
                };

        BinaryOperator<Map<K, List<R>>> combiner =
                (mapToRetain, newValuesToInsert) -> {
                    newValuesToInsert.forEach(
                            (key, values) -> {
                                mapToRetain.putIfAbsent(key, new ArrayList<>());
                                mapToRetain.get(key).addAll(values);
                            });
                    return mapToRetain;
                };

        return Collector.of(
                HashMap::new, accumulator, combiner, Collector.Characteristics.IDENTITY_FINISH);
    }


    public static <L, R> Collector<Either<L, R>, Tuple2<List<L>, List<R>>, Tuple2<List<L>, List<R>>> toEitherList() {

        BiConsumer<Tuple2<List<L>, List<R>>, Either<L, R>> accumulator = (pair, either) -> {
            if (either.isLeft()) {
                pair._1().add(either.getLeft());
            } else {
                pair._2().add(either.get());
            }
        };

        BinaryOperator<Tuple2<List<L>, List<R>>> combiner = (toRetain, toMerge) -> {
            toRetain._2().addAll(toMerge._2());
            toRetain._1().addAll(toMerge._1());
            return toRetain;
        };

        return Collector.of(
                () -> Tuple.of(new ArrayList<>(), new ArrayList<>()),
                accumulator,
                combiner,
                tuple -> Tuple.of(ImmutableList.copyOf(tuple._1()), ImmutableList.copyOf(tuple._2())),
                Collector.Characteristics.IDENTITY_FINISH);
    }

}
