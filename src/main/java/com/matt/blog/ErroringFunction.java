package com.matt.blog;

import io.vavr.control.Either;
import io.vavr.control.Validation;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErroringFunction<T,M,R> implements Function<T, Either<M, R>> {

    private final Predicate<T> condition;
    private final M errorMessage;
    private final Function<T, R> mapper;

    @Override
    public Either<M, R> apply(T t) {
        if (condition.test(t)) {
            return Either.left(errorMessage);
        }
        return Either.right(mapper.apply(t));
    }
}
