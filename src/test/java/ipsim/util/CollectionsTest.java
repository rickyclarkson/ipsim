package ipsim.util;

import fj.F;
import fj.Function;
import fj.data.IterableW;
import java.util.Arrays;
import junit.framework.TestCase;

public class CollectionsTest extends TestCase {
    public void testSize() {
        assertEquals(0, Collections.size(Arrays.asList()));
        assertEquals(1, Collections.size(Arrays.asList("one")));
        assertEquals(3, Collections.size(Arrays.asList(3,4,5)));
    }

    public void testIterableWFilter() {
        for (boolean b: new boolean[]{false, true})
            assertEquals(0, Collections.size(IterableW.wrap(Arrays.asList()).filter(Function.constant(b))));

        assertEquals(2, Collections.size(IterableW.wrap(Arrays.asList(3,2,4,5)).filter(new F<Integer, Boolean>() {
            @Override
            public Boolean f(Integer integer) {
                return integer < 4;
            }
        })));
    }
}