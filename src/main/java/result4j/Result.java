/*
 * Copyright 2019 Hannes De Valkeneer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package result4j;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

public interface Result<T, E> extends Iterable<T> {
    /**
     * Create a success result.
     *
     * @param ok  The value.
     * @param <T> The success type.
     * @param <E> Ignored.
     * @return A success result.
     */
    static <T, E> Result<T, E> ok(final T ok) {
        return new Ok(Objects.requireNonNull(ok));
    }

    /**
     * Create an error result.
     *
     * @param err The error value.
     * @param <T> Ignored.
     * @param <E> The error type.
     * @return An error result.
     */
    static <T, E> Result<T, E> err(final E err) {
        return new Err(Objects.requireNonNull(err));
    }

    /**
     * @return true, if ok result, false, if error result.
     */
    boolean isOk();

    /**
     * @return true, if error result, false, if ok result.
     */
    boolean isErr();


    /**
     * Converts a Result into an Optional
     *
     * @return Optional.empty() if error result.
     */
    Optional<T> ok();

    /**
     * Converts a Result into an Optional
     *
     * @return Optional.empty() if ok result.
     */
    Optional<E> err();


    /**
     * Applies a function to the ok value.
     *
     * @param fun A map function.
     * @param <U> The return type of the map function.
     * @return The new result.
     */
    <U> Result<U, E> map(final Function<T, U> fun);


    /**
     * Applies a function to the ok value or a fallback function to an error.
     *
     * @param fun A map function.
     * @param <U> The return type of the map function.
     * @return The calculated value.
     */
    <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> fun);

    /**
     * Applies a function to the error value.
     *
     * @param fun The mapping function.
     * @param <F> The type of the new error.
     * @return The new result.
     */
    <F> Result<T, F> mapErr(final Function<E, F> fun);

    /**
     * Returns the given result if ok.
     *
     * @param res The result if ok.
     * @param <U> The type of the new ok value.
     * @return The new result if ok or an error result with the same error value.
     */
    <U> Result<U, E> and(final Result<U, E> res);

    /**
     * Applies a function that returns a new error if ok.
     *
     * @param fun A function applied with the ok value.
     * @param <U> The type of the new ok value.
     * @return The new result or a new result with the same error.
     */
    <U> Result<U, E> andThen(final Function<T, Result<U, E>> fun);

    /**
     * @param res A result to be returned if this is an error result.
     * @param <F> the error type if applicable.
     * @return Returns the ok result or the given result.
     */
    <F> Result<T, F> or(final Result<T, F> res);

    /**
     * @param fun a function to be applied on the error value.
     * @param <F> The error type of the return value of the function.
     * @return a new result type if err, else an ok result with the current ok value.
     */
    <F> Result<T, F> orElse(final Function<E, Result<T, F>> fun);

    /**
     * @param def a default value.
     * @return The ok value or a default value.
     */
    T unwrapOr(final T def);

    /**
     * @param def a function to calculate a default value in case of an error.
     * @return The ok value or a default value.
     */
    T unwrapOrElse(final Function<E, T> def);

    /**
     * Expects an ok value or throws a null pointer exception.
     *
     * @return The ok value.
     */
    T unwrap();

    /**
     * Expects an ok value or throws a null pointer exception with the given message.
     *
     * @param msg An error message.
     * @return The ok value.
     */
    T expect(final String msg);

    /**
     * Expects an error or throws a null pointer exception.
     *
     * @return The error value.
     */
    E unwrapErr();

    /**
     * Expects an error or throws a null pointer exception with the given message.
     *
     * @param msg An error message.
     * @return The error value.
     */
    E expectErr(final String msg);

    Stream<T> stream();

    final class Ok<T, E> implements Result<T, E> {
        private T value;

        public Ok(final T value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public Optional<T> ok() {
            return Optional.of(value);
        }

        @Override
        public Optional<E> err() {
            return Optional.empty();
        }

        @Override
        public <U> Result<U, E> map(final Function<T, U> fun) {
            return Result.ok(fun.apply(value));
        }

        @Override
        public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> fun) {
            return fun.apply(value);
        }

        @Override
        public <F> Result<T, F> mapErr(final Function<E, F> fun) {
            return Result.ok(value);
        }

        @Override
        public <U> Result<U, E> and(final Result<U, E> res) {
            return res;
        }

        @Override
        public <U> Result<U, E> andThen(final Function<T, Result<U, E>> fun) {
            return fun.apply(value);
        }

        @Override
        public <F> Result<T, F> or(final Result<T, F> res) {
            return Result.ok(value);
        }

        @Override
        public <F> Result<T, F> orElse(final Function<E, Result<T, F>> fun) {
            return Result.ok(value);
        }

        @Override
        public T unwrapOr(final T def) {
            return value;
        }

        @Override
        public T unwrapOrElse(final Function<E, T> def) {
            return value;
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public T expect(final String msg) {
            return value;
        }

        @Override
        public E unwrapErr() {
            throw new ResultException();
        }

        @Override
        public E expectErr(final String msg) {
            throw new ResultException(msg);
        }

        @Override
        public Stream<T> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean more = true;

                @Override
                public boolean hasNext() {
                    return more;
                }

                @Override
                public T next() {
                    more = false;
                    return value;
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ok<?, ?> ok = (Ok<?, ?>) o;
            return value.equals(ok.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return format("Ok(%s)", value);
        }
    }

    final class Err<T, E> implements Result<T, E> {
        private E value;

        public Err(final E value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public Optional<T> ok() {
            return Optional.empty();
        }

        @Override
        public Optional<E> err() {
            return Optional.of(value);
        }

        @Override
        public <U> Result<U, E> map(final Function<T, U> fun) {
            return Result.err(value);
        }

        @Override
        public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> fun) {
            return fallback.apply(value);
        }

        @Override
        public <F> Result<T, F> mapErr(final Function<E, F> fun) {
            return Result.err(fun.apply(value));
        }

        @Override
        public <U> Result<U, E> and(final Result<U, E> res) {
            return Result.err(value);
        }

        @Override
        public <U> Result<U, E> andThen(final Function<T, Result<U, E>> fun) {
            return Result.err(value);
        }

        @Override
        public <F> Result<T, F> or(final Result<T, F> res) {
            return res;
        }

        @Override
        public <F> Result<T, F> orElse(final Function<E, Result<T, F>> fun) {
            return fun.apply(value);
        }

        @Override
        public T unwrapOr(final T def) {
            return def;
        }

        @Override
        public T unwrapOrElse(final Function<E, T> def) {
            return def.apply(value);
        }

        @Override
        public T unwrap() {
            throw new ResultException();
        }

        @Override
        public T expect(final String msg) {
            throw new ResultException(msg);
        }

        @Override
        public E unwrapErr() {
            return value;
        }

        @Override
        public E expectErr(final String msg) {
            return value;
        }

        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next() {
                    return null;
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Err<?, ?> err = (Err<?, ?>) o;
            return value.equals(err.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return format("Err(%s)", value);
        }
    }

    class ResultException extends RuntimeException {
        public ResultException() {
        }

        public ResultException(final String message) {
            super(message);
        }
    }
}
