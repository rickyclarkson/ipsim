package ipsim.util;

import fj.data.Option;
import junit.framework.TestCase;

import static ipsim.util.SequenceUtility.cons;
import static ipsim.util.SequenceUtility.empty;
import static ipsim.util.SequenceUtility.get;
import static ipsim.util.SequenceUtility.iterable;
import static ipsim.util.SequenceUtility.reverse;
import static ipsim.util.SequenceUtility.size;

public final class SequenceUtilityTest extends TestCase {
    public void testEmpty() {
        assertEquals(0, size(empty()));
    }

    public void testCons() {
        assertEquals(1, size(cons(3, empty())));
    }

    public void testReverse() {
        assertEquals(3, get(reverse(cons(5, cons(4, cons(3, empty())))), 0));
    }

    public void testIterable() {
        final Option<Node<Integer>> list = cons(3, cons(4, cons(5, SequenceUtility.<Integer>empty())));
        int total = 0;
        for (int x: iterable(list))
            total += x;

        assertEquals(3 + 4 + 5, total);
    }

    public void testGet() {
        final Option<Node<Integer>> list = cons(3, cons(4, cons(5, SequenceUtility.<Integer>empty())));
        assertEquals(3, (int)get(list, 0));
        assertEquals(4, (int)get(list, 1));
        assertEquals(5, (int)get(list, 2));
    }
}
