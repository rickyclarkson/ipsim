package ipsim;

import fj.F;
import fj.Function;
import fj.data.Either;
import ipsim.lang.Runnables;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Caster {
    public static boolean isHTMLFrameHyperlinkEvent(final HyperlinkEvent event) {
        return event instanceof HTMLFrameHyperlinkEvent;
    }

    public static <T> F<T, Boolean> equalT(@NotNull final T first) {
        return new F<T, Boolean>() {
            @Override
            public Boolean f(final T second) {
                return first.equals(second);
            }
        };
    }

    public static <A, B extends Exception> A asFirst(final Either<A, B> either) {
        return either.either(Function.<A>identity(), Runnables.<B, A>wrapAndThrow());
    }

    public static <T, R extends T> boolean equalT(@NotNull final T first, @NotNull final R second) {
        return first.equals(second);
    }

    @NotNull
    public static
    <T> T asNotNull(@Nullable final T t) {
        return t == null ? ExceptionHandler.<T>impossible() : t;
    }

    public static <T> F<Object, T> asFunction(final Class<T> aClass) {
        return new F<Object, T>() {
            @Override
            @NotNull
            public T f(@NotNull final Object o) {
                return aClass.cast(o);
            }
        };
    }
}