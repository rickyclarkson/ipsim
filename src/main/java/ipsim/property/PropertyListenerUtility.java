package ipsim.property;

public class PropertyListenerUtility {
    public static <T> PropertyListener<T> fromRunnable(final Runnable runnable) {
        return new PropertyListener<T>() {
            @Override
            public void propertyChanged(final Property<T> property, final T oldValue, final T newValue) {
                runnable.run();
            }
        };
    }
}