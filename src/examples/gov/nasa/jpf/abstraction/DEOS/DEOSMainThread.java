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
class DEOSMainThread extends DEOSThread {
  public DEOSMainThread (Thread t) {
    super(t);
  }

  public void run (int tickResult) {
    DEOS.inc();

    if (tickResult == Clock.NOTIMECHANGE) {
      //System.out.println("Thread: " + thread + " - Depth: " + depth);
      DEOS.println("No interrupts!");
      DEOS.println(thread.toString() + " waiting until next period");
      DEOSKernel.waitUntilNextPeriodK(thread); //yieldCPU();
    } else {
      if (Verify.randomBool()) {
        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        DEOS.println("---Choice 0 of main---");
        DEOS.println(thread.toString() + " waiting until next period");
        DEOSKernel.waitUntilNextPeriodK(thread);

        //yieldCPU();
      } else {
        DEOS.println("---Choice 1 of main---");


        //System.out.println("Thread: " + thread + " - Depth: " + depth);
        getInterrupted(tickResult);
      }
    }
  }
}
