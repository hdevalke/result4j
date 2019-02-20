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
package result4j.v2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A result type that contains either an value or an error.
 * <p>
 * It is inspired by the Resultv2 type in the Rust programming language.
 *
 * @param <T> the ok value
 * @param <E> the error value
 */
public class Resultv2<T, E> implements Iterable<T> {
    private final T ok;
    private final E err;

    private Resultv2(final T ok, final E err) {
        this.ok = ok;
        this.err = err;
    }

    /**
     * @return true, if ok result, false, if error result.
     */
    public boolean isOk() {
        return ok != null;
    }

    /**
     * @return true, if error result, false, if ok result.
     */
    public boolean isErr() {
        return err != null;
    }

    /**
     * Converts a Resultv2 into an Optional
     *
     * @return Optional.empty() if error result.
     */
    public Optional<T> ok() {
        return Optional.ofNullable(ok);
    }

    /**
     * Converts a Resultv2 into an Optional
     *
     * @return Optional.empty() if ok result.
     */
    public Optional<E> err() {
        return Optional.ofNullable(err);
    }

    /**
     * Applies a function to the ok value.
     *
     * @param fun A map function.
     * @param <U> The return type of the map function.
     * @return The new result.
     */
    public <U> Resultv2<U, E> map(final Function<T, U> fun) {
        return ok == null ? err(err) : ok(fun.apply(ok));
    }

    /**
     * Applies a function to the ok value or a fallback function to an error.
     *
     * @param fun A map function.
     * @param <U> The return type of the map function.
     * @return The calculated value.
     */
    public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> fun) {
        return ok == null ? fallback.apply(err) : fun.apply(ok);
    }

    /**
     * Applies a function to the error value.
     *
     * @param fun The mapping function.
     * @param <F> The type of the new error.
     * @return The new result.
     */
    public <F> Resultv2<T, F> mapErr(final Function<E, F> fun) {
        return err == null ? ok(ok) : err(fun.apply(err));
    }

    /**
     * Returns the given result if ok.
     *
     * @param res The result if ok.
     * @param <U> The type of the new ok value.
     * @return The new result if ok or an error result with the same error value.
     */
    public <U> Resultv2<U, E> and(final Resultv2<U, E> res) {
        return ok == null ? err(err) : res;
    }

    /**
     * Applies a function that returns a new error if ok.
     *
     * @param fun A function applied with the ok value.
     * @param <U> The type of the new ok value.
     * @return The new result or a new result with the same error.
     */
    public <U> Resultv2<U, E> andThen(final Function<T, Resultv2<U, E>> fun) {
        return err == null ? fun.apply(ok) : err(err);
    }

    /**
     * @param res A result to be returned if this is an error result.
     * @param <F> the error type if applicable.
     * @return Returns the ok result or the given result.
     */
    public <F> Resultv2<T, F> or(final Resultv2<T, F> res) {
        return ok == null ? res : ok(ok);
    }

    /**
     * @param fun a function to be applied on the error value.
     * @param <F> The error type of the return value of the function.
     * @return a new result type if err, else an ok result with the current ok value.
     */
    public <F> Resultv2<T, F> orElse(final Function<E, Resultv2<T, F>> fun) {
        return ok == null ? fun.apply(err) : ok(ok);
    }

    /**
     * @param def a default value.
     * @return The ok value or a default value.
     */
    public T unwrapOr(T def) {
        return ok == null ? def : ok;
    }

    /**
     * @param def a function to calculate a default value in case of an error.
     * @return The ok value or a default value.
     */
    public T unwrapOrElse(Function<E, T> def) {
        return ok == null ? def.apply(err) : ok;
    }

    /**
     * Expects an ok value or throws a null pointer exception.
     *
     * @return The ok value.
     */
    public T unwrap() {
        return Objects.requireNonNull(ok);
    }

    /**
     * Expects an ok value or throws a null pointer exception with the given message.
     *
     * @param msg An error message.
     * @return The ok value.
     */
    public T expect(final String msg) {
        return Objects.requireNonNull(ok, msg);
    }

    /**
     * Expects an error or throws a null pointer exception.
     *
     * @return The error value.
     */
    public E unwrapErr() {
        return Objects.requireNonNull(err);
    }

    /**
     * Expects an error or throws a null pointer exception with the given message.
     *
     * @param msg An error message.
     * @return The error value.
     */
    public E expectErr(final String msg) {
        return Objects.requireNonNull(err, msg);
    }

    /**
     * Create a success result.
     *
     * @param ok  The value.
     * @param <T> The success type.
     * @param <E> Ignored.
     * @return A success result.
     */
    public static <T, E> Resultv2<T, E> ok(final T ok) {
        Objects.requireNonNull(ok);
        return new Resultv2<>(ok, null);
    }

    /**
     * Create an error result.
     *
     * @param err The error value.
     * @param <T> Ignored.
     * @param <E> The error type.
     * @return An error result.
     */
    public static <T, E> Resultv2<T, E> err(final E err) {
        Objects.requireNonNull(err);
        return new Resultv2<>(null, err);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            boolean more = err == null;

            @Override
            public boolean hasNext() {
                return more;
            }

            @Override
            public T next() {
                more = false;
                return ok;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resultv2<?, ?> result = (Resultv2<?, ?>) o;
        return Objects.equals(ok, result.ok) &&
                Objects.equals(err, result.err);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ok, err);
    }

    @Override
    public String toString() {
        return ok == null ? "Err(" + err + ")" : "Ok(" + ok + ")";
    }
}

