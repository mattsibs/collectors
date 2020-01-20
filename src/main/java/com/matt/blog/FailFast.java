package com.matt.blog;

import com.google.common.base.Suppliers;
import io.vavr.control.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

public class FailFast {

    /**
     * Returns integers as strings.
     * Only accepts odd integers
     *
     * @param in Input integers
     * @return List of integers as strings
     * @throws RuntimeException if any integers are even
     */
    public List<Integer> iterate(final List<Integer> in) {
        for (int integer : in) {
            if (integer % 2 == 0) {
                throw new RuntimeException("I'm an odd guy");
            }

        }
        return in;
    }

    //This is wrong
    public Validation<String, List<Integer>> iterate(final Stream<Integer> integers) {
        return integers
                .collect(FailFast.collect(i -> i % 2 == 0, "I'm an odd guy"));
    }

    public Validation<String, List<Integer>> iterateTrulyLazy(final Stream<Integer> integers) {
        return failFast(integers, i -> i % 2 == 0, "I'm an odd guy")
                .map(integerStream -> integerStream.collect(Collectors.toList()));
    }

    public static <T> Validation<String, Stream<T>> failFast(final Stream<T> stream, final Predicate<T> checker, final String message) {
        return new LazyValidation<>(() -> {
            boolean match = stream.anyMatch(checker);
            //TODO: Rethink this as test fails
            return match ? Validation.invalid(message) : Validation.valid(stream);
        });
    }

    public static <T, M> Collector<T, MutableFail<M, List<T>>, Validation<M, List<T>>> collect(
            final Predicate<T> checker,
            @NonNull final M message) {

        BiConsumer<MutableFail<M, List<T>>, T> accumulator = (fail, t) -> {
            if (checker.test(t)) {
                fail.setFailed(true);
                fail.setFail(message);
            } else {
                fail.getSuccess().add(t);
            }
        };
        BinaryOperator<MutableFail<M, List<T>>> combiner = (f1, f2) -> {
            if (f1.failed) {
                return f1;
            }
            if (f2.failed) {
                return f2;
            }

            f1.success.addAll(f2.success);
            return f1;
        };

        return Collector.of(
                () -> new MutableFail<>(false, null, new ArrayList<>()),
                accumulator,
                combiner,
                MutableFail::toValidation,
                Collector.Characteristics.IDENTITY_FINISH);
    }
    @Data
    @AllArgsConstructor
    private static class MutableFail<F, T> {

        private boolean failed;
        private F fail;

        private T success;
        private Validation<F, T> toValidation() {
            return failed ? Validation.invalid(fail) : Validation.valid(success);
        }

    }

    public static class LazyValidation<T> implements Validation<String, T> {

        private final Supplier<Validation<String, T>> supplier;

        public LazyValidation(Supplier<Validation<String, T>> supplier) {
            this.supplier = Suppliers.memoize(supplier::get);
        }

        @Override
        public boolean isValid() {
            return supplier.get().isValid();
        }

        @Override
        public boolean isInvalid() {
            return supplier.get().isInvalid();
        }

        @Override
        public T get() {
            return supplier.get().get();
        }

        @Override
        public String stringPrefix() {
            return supplier.get().stringPrefix();
        }

        @Override
        public String getError() {
            return supplier.get().getError();
        }
    }
}
