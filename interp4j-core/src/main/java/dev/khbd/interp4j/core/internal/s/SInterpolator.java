package dev.khbd.interp4j.core.internal.s;

/**
 * 'S' interpolator implementation.
 *
 * <p>Note: This class is implementation detail, don't use yourself.
 *
 * @author Sergei_Khadanovich
 * @deprecated This class is deprecated because inlined interpolation
 * is going to be used in the future releases.
 */
@Deprecated(since = "1.0.0", forRemoval = true)
public class SInterpolator {

    /**
     * String literal parts.
     *
     * <p>{@code parts.length} should be more than 0. This constraint is checked at compile time.
     */
    private final String[] parts;

    public SInterpolator(String... parts) {
        this.parts = parts;
    }

    /**
     * Interpolate string parts with specified arguments.
     *
     * <p>Note: {@code args.length} should be equal to {@code parts.length - 1}.
     * This constraint is checked at compile time.
     *
     * @param args expressions to complete interpolation
     * @return interpolation result
     */
    public String interpolate(Object... args) {
        StringBuilder builder = new StringBuilder(parts[0]);

        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            builder.append(parts[i + 1]);
        }

        return builder.toString();
    }
}
