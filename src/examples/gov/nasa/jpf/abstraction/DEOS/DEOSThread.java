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

import gov.nasa.jpf.jvm.Verify;


/**
 * DOCUMENT ME!
 */
class DEOSThread {
  Thread  thread; // which "Thread" object is this Thread one related to
  boolean running = false;

  // run at a time
  boolean isMain = false;
  boolean isIdle = false;
  boolean firstTime = true;
  boolean setDelete = false;
  boolean setWaitUntilNextPeriod = false;

  public DEOSThread (Thread th) {
    thread = th;
    isIdle = thread.isIdle();
    isMain = thread.isMain();

    System.out.println(thread.toString() + " created");
  }

  public void run (int tickResult) {
    //Verify.assert(Timer.timer || Timer.tick);
    DEOS.inc();

    if (tickResult == Clock.NOTIMECHANGE) {
      if (Verify.randomBool()) {
        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("No interrupt - Choice 0:");
        DEOS.println(thread.toString() + " waiting until next period");
        DEOSKernel.waitUntilNextPeriodK(thread);

        //yieldCPU();
      } else {
        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("No interrupt - Choice 1:");
        DEOS.println(thread.toString() + " deleting");
        DEOSKernel.deleteThreadK(thread); //deleteThread();
      }
    } else {
      switch (Verify.random(2)) {
      case 0:

        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("Choice 0:");
        DEOS.println(thread.toString() + " waiting until next period");
        DEOSKernel.waitUntilNextPeriodK(thread);

        //yieldCPU();	   
        break;

      case 2:

        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("Choice 2:");
        DEOS.println(thread.toString() + " deleting");
        DEOSKernel.deleteThreadK(thread);

        //deleteThread();
        break;

      case 1:

        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("Choice 1: ");
        getInterrupted(tickResult);

        break;
      }
    }
  }

  // Modified by ckong - June 26, 2001
  void getInterrupted (int tickResult) {
    if (tickResult == Clock.SYSTEMINTERRUPT) {
      DEOS.println(thread.toString() + " interrupted by system tick");
      DEOS.thePeriodicClock.resetUsedTime();
      Scheduler.handleSystemTickInterrupt();
    } else if (tickResult == Clock.TIMEOUT) {
      DEOS.println(thread.toString() + " interrupted by timer");
      Scheduler.handleTimerInterrupt();
    } else {
      DEOS.println(thread.toString() + " waiting for time to pass");
    }
  }
}
