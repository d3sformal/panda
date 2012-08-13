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
class StartOfPeriodEvent {
  //static int[]   startOfPeriodTickValues;
  static StartOfPeriodEvent[] periodicEvents;
  int                         itsPeriodId;
  int                         itsPassCount;
  int                         countDown;
  int                         itsPeriodIndex;
  threadList                  itsWaitingThreads;
  StartOfPeriodEvent          itsSuccessor;

  private StartOfPeriodEvent (int thePeriodIndex, int thePassCount) {
    itsPassCount = thePassCount;
    itsPeriodIndex = thePeriodIndex;
    itsWaitingThreads = new threadList();

    countDown = 1;
    itsPeriodId = 0;
    itsSuccessor = null;
  }

  public int currentPeriod () {
    return itsPeriodId;
  }

  //public static int[]  startOfPeriodTickValueArray() {
  //  return startOfPeriodTickValues;
  //}
  public static StartOfPeriodEvent eventForPeriodIndex (int i) {
    return periodicEvents[i];
  }

  public static void initialize () {
    //System.out.println("StartOfPeriodEvent.Initialize");
    int numPeriods = Registry.numPeriods;


    //SPIN Registry::numberOfPeriodsSupported();
    periodicEvents = new StartOfPeriodEvent[numPeriods];

    int ticksInLastPeriod = 1;

    for (int i = 0; i < numPeriods; i++) {
      int ticksInThisPeriod = Registry.periodDurationInSystemTicks(i);
      periodicEvents[i] = new StartOfPeriodEvent(i, 
                                                 ticksInThisPeriod / ticksInLastPeriod);

      if (i > 0) {
        periodicEvents[i - 1].itsSuccessor = periodicEvents[i];
      }

      ticksInLastPeriod = ticksInThisPeriod;
    }

    //startOfPeriodTickValues = new int[numPeriods];
    //for (int i=0; i<numPeriods; i++) {
    //  startOfPeriodTickValues[i] = 0;
    //}
  }

  public void makeThreadWait (Thread theThread) {
    //System.out.println("StartOfPeriod(" + itsPeriodIndex + 
    //                    ").makeThreadWait");
    itsWaitingThreads.addAtEnd(theThread.startOfPeriodWaitNode);
  }

  public void pulseEvent (int systemTickCount) {
    countDown = countDown - 1;

    //DEOS.println("StartOfPeriod.pulseEvent " + itsPeriodIndex +
    //						 " countDown = " + countDown);
    if (countDown == 0) {
      itsPeriodId = (itsPeriodId + 1) % 2; /////!!!!!!!


      //startOfPeriodTickValues[itsPeriodIndex] = systemTickCount;
      countDown = itsPassCount;
      Scheduler.runnableList().mergeList(itsWaitingThreads);

      if (itsSuccessor != null) {
        itsSuccessor.pulseEvent(systemTickCount);
      }
    }
  }
}
