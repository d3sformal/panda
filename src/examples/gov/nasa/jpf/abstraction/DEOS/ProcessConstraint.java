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
class ProcessConstraint {
  int remainingNormalizedUtilization;
  int initialNormalizedUtilization;

  public ProcessConstraint (int theUtilization) {
    //System.out.println("ProcessConstraint Constructor");
    initialNormalizedUtilization = theUtilization;
    remainingNormalizedUtilization = theUtilization;
  }

  public static int CPUTimeToNormalizedUtilization (int CPUTime, 
                                                    int periodIndex) {
    int ticksPerPeriod = (Registry.periodDurationInMicroSecs(periodIndex) / Registry.periodDurationInMicroSecs(0));

    //System.out.println("CPUTime " + CPUTime + " NormTime " + 
    //          ((CPUTime + ticksPerPeriod -1) / ticksPerPeriod));
    //return (CPUTime + ticksPerPeriod -1) / ticksPerPeriod;
    return CPUTime / ticksPerPeriod;
  }

  public boolean allocateCPUForThread (int CPUTime, int periodIndex, 
                                       boolean isISR) {
    int normalizedThreadUtilization = CPUTimeToNormalizedUtilization(CPUTime, 
                                                                     periodIndex);

    //System.out.println("ProcessConstraint.allocateCPUForThread " + 
    //                 normalizedThreadUtilization + "(= norm)vs(remain =)"
    //                 + remainingNormalizedUtilization);
    if (normalizedThreadUtilization > remainingNormalizedUtilization) {
      return false; // InsufficientCPU
    }

    remainingNormalizedUtilization -= normalizedThreadUtilization;

    return true;
  }

  public void deallocateCPUForThread (int CPUTime, int periodIndex) {
    remainingNormalizedUtilization += CPUTimeToNormalizedUtilization(CPUTime, 
                                                                     periodIndex);
  }

  public int oneHundredPercentUtilization () {
    return Registry.periodDurationInMicroSecs(0);
  }
}
