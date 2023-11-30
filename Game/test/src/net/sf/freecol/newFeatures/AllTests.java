package net.sf.freecol.newFeatures;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sf.freecol.newFeatures");
        //$JUnit-BEGIN$
        suite.addTestSuite(WagonTest.class);
        //$JUnit-END$
        return suite;
    }
}
