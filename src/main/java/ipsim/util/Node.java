package ipsim.util;

import fj.data.Option;

public class Node<T> {
    public final T car;
    public final Option<Node<T>> cdr;

    public Node(T car, Option<Node<T>> cdr) {

        this.car = car;
        this.cdr = cdr;
    }
}
