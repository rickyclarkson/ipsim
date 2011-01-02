package ipsim.lang;

import com.rickyclarkson.testsuite.UnitTest;
import fj.P1;

public class DynamicVariable<T> {
    private T t;

    public DynamicVariable(final T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public <R> R withValue(final T newT, final P1<R> lazy) {
        final T before = t;
        t = newT;
        try {
            return lazy._1();
        } finally {
            t = before;
        }
    }

    public static final UnitTest testDynamicVariable = new UnitTest() {
        @Override
        public boolean invoke() {
            final DynamicVariable<String> var = new DynamicVariable<String>("hello");

            return var.withValue("goodbye", new P1<Boolean>() {
                @Override
                public Boolean _1() {
                    return var.get().equals("goodbye");
                }
            }) && var.get().equals("hello");
        }

        public String toString() {
            return "testDynamicVariable";
        }
    };
}