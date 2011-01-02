package com.rickyclarkson.testsuite;

import com.rickyclarkson.java.lang.Throwables;
import java.io.PrintStream;

public final class UnitTestUtility {
    public static boolean runTests(final PrintStream out, final UnitTest... tests) {
        int passed = 0;
        int failed = 0;

        for (final UnitTest test : tests) {
            try {
                if (test.invoke()) {
                    out.println(test.toString() + " passed");
                    passed++;
                } else {
                    out.println(test.toString() + " failed");
                    failed++;
                }
            } catch (final RuntimeException exception) {
                out.println(Throwables.toString(exception));
                out.println(test.toString() + " failed");
                failed++;
            }
        }

        out.println(passed + " passed");
        out.println(failed + " failed");

        return failed == 0;
    }
}