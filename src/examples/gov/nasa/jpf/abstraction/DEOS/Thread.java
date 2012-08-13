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
class Thread {
  /*
     static void stopAndDeleteThreadKernelExceptionHandler() {
       Thread  theThread = Scheduler.currentThread();
       DEOSProcess ownerProcess =  Scheduler.currentProcess();
     
       int interruptState = CPU.enterCritical();
       theThread.stopThread();
       theThread.itsCreationStatus = threadStatusNotCreated;
       ownerProcess.deallocateCPUBudgetForThread( theThread );
       theThread = null;
       Scheduler.scheduleOtherThread();
       CPU.exitCritical( interruptState );
     }
   */
  static final int   threadStatusNotCreated = 0;
  static final int   threadStatusDormant = 1;
  static final int   threadStatusActive = 2;
  static final int   threadStatusKernelExceptionPending = 3;
  threadListNode     timeoutNode;
  threadListNode     startOfPeriodWaitNode;
  threadListNode     preemptionNode;
  Budget             itsBudget;
  Budget             itsCurrentBudget;
  int                itsLastExecution;
  int                itsLastCompletion;
  StartOfPeriodEvent itsPeriodicEvent;
  int                itsPeriodIndex;
  int                itsCurrentPriority;
  int                itsCreationStatus;
  String             itsName;
  DEOSThread         body;

  public Thread (String name) {
    //System.out.println("Thread Constructor");
    itsName = name;

    timeoutNode = new threadListNode(this);
    startOfPeriodWaitNode = new threadListNode(this);
    preemptionNode = new threadListNode(this);


    // %%%%%% added this see athread.spin
    itsBudget = new Budget();
    itsCreationStatus = threadStatusNotCreated;

    Assertion.addThread(this);

    if (name.equals("main")) {
      body = new DEOSMainThread(this);
    } else if (name.equals("idle")) {
      body = new DEOSIdleThread(this);
    } else {
      body = new DEOSThread(this);
    }
  }

  public DEOSThread getBody () {
    return body;
  }

  public void setCPUBudget (int b) {
    //System.out.println("Thread.setCPUBudget " + b);
    itsBudget.setTotalBudgetInUsec(b);
  }

  public void setCurrentPriority (int p) {
    //System.out.println("Thread.setCurrentPriority " + p);
    itsCurrentPriority = p;
  }

  public boolean isIdle () {
    return itsName.equals("idle");
  }

  public boolean isMain () {
    return itsName.equals("main");
  }

  public boolean ConceptualObjectConstructor (int period) {
    itsPeriodIndex = period;
    itsCurrentPriority = Scheduler.priorityForPeriodIndex(itsPeriodIndex);
    itsPeriodicEvent = StartOfPeriodEvent.eventForPeriodIndex(itsPeriodIndex);
    itsCurrentBudget = itsBudget;
    itsCreationStatus = threadStatusDormant;

    return true;
  }

  public void ConceptualObjectDestructor () {
    itsCreationStatus = threadStatusNotCreated;
  }

  public Budget budget () {
    return itsBudget;
  }

  public void completeForPeriod () {
    //Verify.beginAtomic();
    waitForNextTriggeringEvent();
    itsLastCompletion = itsPeriodicEvent.currentPeriod();

    //Verify.endAtomic();
  }

  public void cpuAllowanceExceeded () {
    if (this == Scheduler.idleThread()) {
      //System.out.println("CPUAllowance Exceeded, but it is idle thread");
      startChargingCPUTime();

      //System.out.println("after");
    } else {
      waitForNextPeriod();
    }
  }

  public int cpuBudget () {
    return itsBudget.totalBudgetInUsec();
  }

  public int currentPriority () {
    return itsCurrentPriority;
  }

  public void initiateStopAndDelete () {
    //    Verify.beginAtomic();
    Thread current = Scheduler.currentThread();

    if (current != this) {
      System.out.println("Current running thread (" + current + 
                         ") != thread trying to delete itself!" + this);

      return;
    }

    current.stopThread();
    current.itsCreationStatus = threadStatusNotCreated;
    Scheduler.currentProcess().deallocateCPUBudgetForThread(current);


    //    Verify.endAtomic();
    Scheduler.scheduleOtherThread();
  }

  public int periodIndex () {
    return itsPeriodIndex;
  }

  public void startChargingCPUTime () {
    //Verify.beginAtomic();
    //System.out.println("Thread.startChargingCPUTime");
    int cp = itsPeriodicEvent.currentPeriod();
    int budget;

    // added by ckong - July 3, 2001
    if (isIdle()) {
      budget = itsCurrentBudget.totalBudgetInUsec();
    } else {
      if (cp == itsLastExecution) {
        //sop.currentId == this.itsLastExecution
        budget = itsCurrentBudget.remainingBudgetInUsec();
      } else {
        budget = itsCurrentBudget.totalBudgetInUsec();
        itsLastExecution = cp;

        //int remainingTime = itsCurrentBudget.remainingBudgetInUsec();
      }
    }

    itsCurrentBudget.setRemainingBudgetInUsec(budget);


    //    Verify.endAtomic();
    Assertion.check();

    itsCurrentBudget.startTimer();
  }

  public void startThread (int theCPUBudget) {
    //System.out.println("Thread.StartThread");
    itsCurrentPriority = Scheduler.priorityForPeriodIndex(itsPeriodIndex);
    itsBudget.setTotalBudgetInUsec(theCPUBudget);
    startThreadInternal();
    itsLastCompletion = itsPeriodicEvent.currentPeriod() - 1;
    waitForNextTriggeringEvent(); // assumes critical!
    itsLastExecution = itsPeriodicEvent.currentPeriod();
    itsLastCompletion = itsPeriodicEvent.currentPeriod();
  }

  public void startThreadInternal () {
    itsCreationStatus = threadStatusActive;
    itsBudget.setRemainingBudgetInUsec(itsBudget.totalBudgetInUsec());
    itsCurrentBudget = itsBudget;
  }

  public void stopChargingCPUTime (int bonus) {
    //Verify.beginAtomic();
    //System.out.println("Thread.stopChargingCPUTime");
    // Modified by ckong - June 25, 2001
    //int remainingTime = bonus + DEOS.theTimer.timeRemaining();
    int remainingTime = bonus + 
                        DEOS.theTimer.getRemainingTime(
                              DEOS.systemClock.getCurrentTime());
    itsCurrentBudget.setRemainingBudgetInUsec(remainingTime);

    //body.stopThread();
    //Verify.endAtomic();
  }

  public void stopThread () {
    //    Verify.beginAtomic();
    itsLastCompletion = itsPeriodicEvent.currentPeriod();
    itsCreationStatus = threadStatusDormant;
    Assertion.removeThread(this);

    //    Verify.endAtomic();
  }

  public String toString () {
    return itsName;
  }

  public void waitForNextPeriod () {
    //    Verify.beginAtomic();    
    int interruptState = CPU.enterCritical();
    completeForPeriod();


    //    Verify.endAtomic();
    Scheduler.scheduleOtherThread();
    CPU.exitCritical(interruptState);
  }

  public void waitForNextTriggeringEvent () {
    //System.out.println("Thread.waitForNextTriggeringEvent");
    itsPeriodicEvent.makeThreadWait(this);
  }
  
  // remove unused import warning
  static Object V = Verify.class;
}
