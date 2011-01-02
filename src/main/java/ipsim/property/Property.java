package ipsim.property;

import org.jetbrains.annotations.NotNull;

public interface Property<T> {
    @NotNull
    T get();

    void set(@NotNull final T newValue);

    void addPropertyListener(@NotNull final PropertyListener<T> propertyListener);
}