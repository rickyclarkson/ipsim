package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;

public final class ManualTests {
    public static final UnitTest doesntSetTitle = new UnitTest() {
        @Override
        public boolean invoke() {
            //doesn't set the title of the frame when the user loads a file.
            //I broke this on purpose during a refactor, because it won't work with multiple networks anyway.
            return false;
        }

        public String toString() {
            return "doesntSetTitle";
        }
    };
    public static final UnitTest conformanceTestsShowLiteralHTML = new UnitTest() {
        @Override
        public boolean invoke() {
            //conformance, in the GUI, shows some literal <p>s instead of linebreaks.
            return false;
        }

        public String toString() {
            return "Conformance tests show literal HTML";
        }
    };
}