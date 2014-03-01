//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.abstraction.DEOS;

import gov.nasa.jpf.vm.Verify;

// To choose the abstracted version, call DEOS abstraction
// Abstract Pathfinder is used in modified Assertion.java with DEOS.jpf

/**
 * DOCUMENT ME!
 */
class DEOS {
  // Type of timer changed to DecreasingTimer by ckong - June 25, 2001
  static NewTimer      theTimer;
  static PeriodicClock thePeriodicClock;
  static Clock         systemClock;
  static boolean       abstraction = false;
  static String        indent = "";
  static String        INC = "  ";

  //static String INC = "";
  public static void inc () {
    //indent =  INC + indent;
  }

  public static void main (String[] args) {
    Verify.beginAtomic();

    if (args.length == 1) {
      if (args[0].equals("abstraction")) {
        abstraction = true;
      }
    }

    System.out.println("JAVA - DEOS\n");

    theTimer = new NewTimer();
    thePeriodicClock = new PeriodicClock(Registry.uSecsInFastestPeriod);
    systemClock = new Clock(thePeriodicClock, theTimer);

    DEOSKernel.coldStartKernel();

    int result = DEOSKernel.createThreadK("user1", 0, 21, 1);

    if (false) System.out.println("Thread created status = " + result);
    
    result = DEOSKernel.createThreadK("user2", 0, 21, 1);

    if (false) System.out.println("Thread created status = " + result);
    
    int tickResult = Clock.NOINTERRUPTS;

    Verify.endAtomic();

    while (true) {
      Verify.beginAtomic();


      // added ckong - June 21, 2001
      DEOS.println("*************************");
      DEOS.println("Current time: " + DEOS.systemClock.getCurrentTime());

      DEOS.println(Scheduler.currentThread().toString() + 
                   " is the current thread");

      tickResult = DEOS.systemClock.ticks();

      Scheduler.currentThread().getBody().run(tickResult);

      Verify.endAtomic();
    }
  }

  public static void println (java.lang.String s) {
    System.out.println(indent + s);
  }
}
