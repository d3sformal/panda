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


/**
 * DOCUMENT ME!
 */
class Scheduler {
  static Thread                itsRunningThread;
  static Thread                itsIdleThread;
  static PriorityListOfThreads itsRunnableList;

  //static int systemTickCount = 0xFFFFFFFF ; 
  static DEOSProcess theProcess;
  static SynchObject synch;

  public static DEOSProcess currentProcess () {
    return theProcess;
  }

  public static Thread currentThread () {
    return itsRunningThread;
  }

  public static Thread idleThread () {
    return itsIdleThread;
  }

  public static void initialize () {
    // JAVA DEOS
    synch = new SynchObject();


    //System.out.println("Scheduler.initialize");
    itsRunnableList = new PriorityListOfThreads();
    initializeIdleProcess();

    int interruptState = CPU.enterCritical();
    itsRunningThread = itsIdleThread;
    itsRunningThread.startChargingCPUTime();
    CPU.exitCritical(interruptState);
    theProcess = new DEOSProcess();
    DEOSKernel.localStartThread(theProcess.mainThread(), 
                                Registry.uSecsInFastestPeriod, 0);
  }

  public static int priorityForPeriodIndex (int thePeriodIndex) {
    return Registry.numPeriods - thePeriodIndex;
  }

  public static PriorityListOfThreads runnableList () {
    return itsRunnableList;
  }

  public static void scheduleAnyThread () {
    //System.out.println("Scheduler.scheduleAnyThread: current = " + 
    //      itsRunningThread);
    if (!itsRunnableList.isEmpty()) {
      if (itsRunningThread.currentPriority() < itsRunnableList.head().parent()
                                                              .currentPriority()) {
        //System.out.println("Preemption");
        itsRunnableList.addAtBeginning(itsRunningThread.preemptionNode);
        Scheduler.scheduleOtherThread();
      } else {
        //System.out.println("Running has Higher Priority, i.e. no Preemption");
        itsRunningThread.stopChargingCPUTime(0);
        itsRunningThread.startChargingCPUTime();
      }
    } else {
      System.out.println("How can this be!!!");
    }
  }

  public static void scheduleOtherThread () {
    Thread newThread;
    Thread fromThread = itsRunningThread;

    //  Verify.beginAtomic();
    //System.out.println("Scheduler.scheduleOtherThread");
    threadListNode runnableThreadListNode = itsRunnableList.head();
    Thread         runnableThread = runnableThreadListNode.parent();
    newThread = runnableThread;
    runnableThreadListNode.removeFromList();


    //  Verify.endAtomic();
    fromThread.stopChargingCPUTime(0);


    //Verify.endAtomic();
    newThread.startChargingCPUTime(); // cannot add this to atomic, since


    // it can block
    // JAVA DEOS - All the threads wait on the synch object.  To wake up
    // the newThread we interrupt() it.  Since it was waiting on the
    // synch object and the fromThread has the lock on the synch object,
    // the new thread won't run until we call wait().  This is intended
    // to only allow on thread to be running at a time like in the real
    // DEOS. - penix
    itsRunningThread = newThread;

    //System.out.println(fromThread + " waking up " + newThread);
    // check to make sure the current thread is not also the new
    // thread or no one will get interrupted...
    //if (fromThread != newThread) {
    //	newThread.javaThread().interrupt();  
    //	try{synch.wait();} 
    //	catch(InterruptedException ex) {
    //		//System.out.println("Waking up again");
    //	};
    //}
  }

  //private
  static void handleSystemTickInterrupt () {
    //System.out.println("Scheduler.handleSystemTickInterrupt");
    StartOfPeriodEvent.eventForPeriodIndex(0).pulseEvent(0);
    Scheduler.scheduleAnyThread();

    // System.out.println("Scheduler.handleSystemTickInterrupt RETURN");
  }

  //private
  static void handleTimerInterrupt () {
    //System.out.println("Scheduler.handleTimerInterrupt");
    itsRunningThread.cpuAllowanceExceeded();
  }

  static void idleThreadMain () {
    while (true) {
    }
  }

  private static void initializeIdleProcess () {
    //System.out.println("Scheduler.initializeIdleProcess");
    itsIdleThread = new Thread("idle");


    // %%%%%5 added this code to make it work see sched.spin file
    itsIdleThread.ConceptualObjectConstructor(0);
    itsIdleThread.setCurrentPriority(0);
    itsIdleThread.waitForNextTriggeringEvent();

    itsIdleThread.startOfPeriodWaitNode.removeFromList();


    //SPIN Registry::periodDurationInMicroSecs(itsIdleThread->periodIndex())
    itsIdleThread.setCPUBudget(Registry.periodDurationInMicroSecs(
                                     itsIdleThread.periodIndex()));
    itsIdleThread.budget().replenish();
  }
  
  // remove unused import warning
  static Object V = Verify.class;
}
