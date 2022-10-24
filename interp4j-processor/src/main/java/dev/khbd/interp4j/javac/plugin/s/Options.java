package dev.khbd.interp4j.javac.plugin.s;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Sergei_Khadanovich
 */
@Getter
public class Options {

    private static final Key<Boolean> PRETTY_PRINTING_ENABLED =
            new Key<>("prettyPrint.after.interpolation", Boolean::parseBoolean, () -> false);

    private static final Key<Boolean> INLINED_INTERPOLATION_ENABLED =
            new Key<>("interpolation.inlined", Boolean::parseBoolean, () -> false);

    private static final List<Key<?>> KEYS = List.of(
            PRETTY_PRINTING_ENABLED,
            INLINED_INTERPOLATION_ENABLED
    );

    @EqualsAndHashCode(of = "name")
    @RequiredArgsConstructor
    private static class Key<V> {

        @Getter
        final String name;
        final Function<String, ? extends V> parser;
        final Supplier<? extends V> defaultValueSupplier;

        V parse(String str) {
            return parser.apply(str);
        }

        V defaultValue() {
            return defaultValueSupplier.get();
        }
    }

    private final Map<Key<?>, Object> params = new HashMap<>();

    public Options(String... args) {
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length != 2) {
                continue;
            }
            Key<?> key = findKey(parts[0]);
            if (Objects.isNull(key)) {
                continue;
            }
            params.put(key, parseValueOrDefault(key, parts[1]));
        }
    }

    private static Object parseValueOrDefault(Key<?> key, String str) {
        try {
            return key.parse(str);
        } catch (Exception e) {
            return key.defaultValue();
        }
    }

    private static Key<?> findKey(String str) {
        return KEYS.stream()
                .filter(key -> key.getName().equals(str))
                .findFirst()
                .orElse(null);
    }

    public boolean prettyPrintAfterInterpolationEnabled() {
        return getKeyValue(PRETTY_PRINTING_ENABLED);
    }

    public boolean inlinedInterpolationEnabled() {
        return getKeyValue(INLINED_INTERPOLATION_ENABLED);
    }

    @SuppressWarnings("unchecked")
    private <T> T getKeyValue(Key<T> key) {
        return (T) params.getOrDefault(key, key.defaultValue());
    }
}
