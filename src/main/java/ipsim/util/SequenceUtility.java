package ipsim.util;

import fj.F2;
import fj.data.Option;
import java.util.Iterator;

public class SequenceUtility {
    public static <T> Option<Node<T>> empty() {
        return Option.none();
    }

    public static <T> Option<Node<T>> cons(T t, Option<Node<T>> sequence) {
        return Option.some(new Node<T>(t, sequence));
    }

    public static <T> F2<T, Option<Node<T>>, Option<Node<T>>> cons() {
        return new F2<T, Option<Node<T>>, Option<Node<T>>>() {
            @Override
            public Option<Node<T>> f(T t, Option<Node<T>> nodeOption) {
                return cons(t, nodeOption);
            }
        };
    }
    public static <T> Option<Node<T>> reverse(Option<Node<T>> sequence) {
        return foldLeft(sequence, SequenceUtility.<T>cons(), SequenceUtility.<T>empty());
    }

    public static <T> int size(Option<Node<T>> sequence) {
        return foldLeft(sequence, new F2<T, Integer, Integer>() {
            @Override
            public Integer f(T t, Integer integer) {
                return integer + 1;
            }
        }, 1);
    }

    public static <T, R> R foldLeft(Option<Node<T>> sequence, F2<T, R, R> operator, R defaultValue) {
        while (true) {
            if (sequence.isNone())
                return defaultValue;

            sequence = sequence.some().cdr;
            defaultValue = operator.f(sequence.some().car, defaultValue);
        }
    }

    public static <T> Iterable<T> iterable(final Option<Node<T>> sequence) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Option<Node<T>> current = sequence;

                    @Override
                    public boolean hasNext() {
                        return current.isSome();
                    }

                    @Override
                    public T next() {
                        try {
                            return current.some().car;
                        } finally {
                            current = current.some().cdr;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T> T get(Option<Node<T>> sequence, int index) {
        while (true) {
            if (index == 0)
                return sequence.some().car;
            sequence = sequence.some().cdr;
            index--;
        }
    }
}
