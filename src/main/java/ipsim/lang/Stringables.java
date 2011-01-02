package ipsim.lang;

import fj.F;
import org.jetbrains.annotations.NotNull;

public class Stringables {
    public static <T extends Stringable> F<T, String> asString() {
        return new F<T, String>() {
            @Override
            @NotNull
            public String f(@NotNull final T stringable) {
                return stringable.asString();
            }
        };
    }
}
