package ipsim.property;

public interface SimpleProperty<T> {
    T get();

    void set(T t);
}
