package org.forritan.talvmenni.bitboard.knowledge;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

   public static Test suite() {
      TestSuite suite = new TestSuite(
         "Test for org.forritan.shallowsenior.bitboard.attacks");
      //$JUnit-BEGIN$
      suite.addTestSuite(PositionTest.class);
      suite.addTestSuite(HistoryHeuristicTest.class);
      //$JUnit-END$
      return suite;
   }
}