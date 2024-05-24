package dev.khbd.interp4j.javac.plugin;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;

/**
 * Result data type.
 *
 * @author Sergei Khadanovich
 */
@ToString
@EqualsAndHashCode
class Result<E, V> {

    private final E error;
    private final V value;

    private Result(E error, V value) {
        this.error = error;
        this.value = value;
    }

    /**
     * Get error.
     */
    E getError() {
        if (isError()) {
            return error;
        }
        throw new IllegalStateException("Result is not error");
    }

    /**
     * Get value.
     */
    V getValue() {
        if (!isError()) {
            return value;
        }
        throw new IllegalStateException("Result is not success");
    }

    /**
     * Check is result is error or not.
     */
    boolean isError() {
        return Objects.nonNull(error);
    }

    /**
     * Create success result.
     *
     * @param value value
     */
    static <E, V> Result<E, V> success(V value) {
        return new Result<>(null, value);
    }

    /**
     * Create error result.
     *
     * @param error error
     */
    static <E, V> Result<E, V> error(E error) {
        return new Result<>(error, null);
    }
}
