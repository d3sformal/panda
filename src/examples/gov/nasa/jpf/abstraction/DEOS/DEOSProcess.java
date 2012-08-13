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

/**
 * DOCUMENT ME!
 */
class DEOSProcess {
  ProcessConstraint itsProcessConstraint;
  Thread            itsMainThread;
  static int        cpuUtilization = Registry.uSecsInFastestPeriod;

  public DEOSProcess () {
    //System.out.println("Process Constructor");
    itsMainThread = new Thread("main");
    itsMainThread.setCPUBudget(Registry.uSecsInFastestPeriod);
    itsMainThread.ConceptualObjectConstructor(0);
    itsProcessConstraint = new ProcessConstraint(cpuUtilization);
  }

  // note - had to change this code because you can't pass an integer
  // by reference in Java - so not, it returns -1 where it used to
  // return false.  the code in DEOSKernel.java (schedk.cpp) has also
  // been changed to reflect this. -jp
  public int allocateCPUBudgetForThread (Thread theThread, 
                                                int requestedBudget, 
                                                int periodIndex) {
    //System.out.println("Process.allocateCPUBudgetForThread");
    int grantedCPU;

    if (theThread == itsMainThread) {
      return itsMainThread.cpuBudget();
    }

    boolean result = itsProcessConstraint.allocateCPUForThread(requestedBudget, 
                                                               periodIndex, 
                                                               false/*SPIN isAssociatedWithInterrupt*/
                                                              );

    if (result) {
      itsMainThread.setCPUBudget(itsMainThread.cpuBudget() - 
                                 ProcessConstraint.CPUTimeToNormalizedUtilization(
                                       requestedBudget, periodIndex));
      grantedCPU = requestedBudget;
    } else {
      grantedCPU = -1;
    }

    return grantedCPU;
  }

  public void deallocateCPUBudgetForThread (Thread theThread) {
    int budget = theThread.cpuBudget();
    int periodIndex = theThread.periodIndex();
    itsProcessConstraint.deallocateCPUForThread(budget, periodIndex);
    itsMainThread.setCPUBudget(itsMainThread.cpuBudget() + 
                               ProcessConstraint.CPUTimeToNormalizedUtilization(
                                     budget, periodIndex));
  }

  public Thread mainThread () {
    return itsMainThread;
  }
}
