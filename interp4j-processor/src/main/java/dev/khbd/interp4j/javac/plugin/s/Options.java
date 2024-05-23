package dev.khbd.interp4j.javac.plugin.s;

import lombok.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Sergei_Khadanovich
 */
public class Options {

    private static final OptionsKey<Boolean> PRETTY_PRINTING_ENABLED = new OptionsKey<>("prettyPrint.after.interpolation");

    private static final List<OptionsDescription<?>> DESCRIPTIONS = Collections.singletonList(
            new OptionsDescription<>(PRETTY_PRINTING_ENABLED, Boolean::parseBoolean, () -> false)
    );

    private final Map<OptionsKey<?>, Object> params = new HashMap<>();

    public Options(String... args) {
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length != 2) {
                continue;
            }
            OptionsDescription<?> description = findDescription(new OptionsKey<>(parts[0]));
            if (Objects.isNull(description)) {
                continue;
            }
            params.put(description.getKey(), parseValueOrDefault(description, parts[1]));
        }
        for (OptionsDescription<?> description : DESCRIPTIONS) {
            params.putIfAbsent(description.getKey(), description.getDefaultValue().get());
        }
    }

    private static OptionsDescription<?> findDescription(OptionsKey<?> key) {
        return DESCRIPTIONS.stream()
                .filter(description -> description.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    private static Object parseValueOrDefault(OptionsDescription<?> description, String value) {
        try {
            return description.getParser().apply(value);
        } catch (Exception e) {
            return description.getDefaultValue().get();
        }
    }

    public boolean prettyPrintAfterInterpolationEnabled() {
        return getKeyValue(PRETTY_PRINTING_ENABLED);
    }

    @SuppressWarnings("unchecked")
    private <T> T getKeyValue(OptionsKey<T> key) {
        return (T) params.get(key);
    }

    @Value
    private static class OptionsKey<V> {
        String name;
    }

    @Value
    private static class OptionsDescription<V> {
        OptionsKey<V> key;
        Function<String, ? extends V> parser;
        Supplier<? extends V> defaultValue;
    }

}
