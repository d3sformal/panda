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
class DEOSKernel {
  //typedef enum
  //{
  public static int threadSuccess = 0;
  public static int threadInvalidHandle = 1;
  public static int threadInvalidInterrupt = 2;
  public static int threadNotDormant = 3;
  public static int threadNotSchedulable = 4;
  public static int threadInsufficientPrivilege = 5;
  public static int threadNotDynamic = 6;
  public static int threadNotStatic = 7;
  public static int threadMaximumThreadsExceeded = 8;
  public static int threadInsufficientRAMForStack = 9;
  public static int threadNoSuchThread = 10;
  public static int threadInvalidTemplate = 11;
  public static int threadNotActive = 12;
  public static int threadInScheduleBefore = 13;
  public static int threadInsufficientBudget = 14;
  public static int threadDuplicateISR = 15;
  public static int threadInvalidFromDynamicProcess = 16;
  public static int threadPrimaryCannotBeISR = 17;

  static void coldStartKernel () {
    //System.out.println("DEOSKernel.coldStartKernel");		
    // Must be done before Scheduler.
    StartOfPeriodEvent.initialize();


    // Must be done after System.
    // Scheduler initialize doesn't return unless we get a shutdown()
    Scheduler.initialize();

    //    System.out.println("DEOSKernel: Finished Initialization");	
  }

  static int createThreadK (String name, int threadTemplateId, int threadBudget, 
                            int periodIndex) {
    //System.out.println("API: createThreadK Period " + periodIndex + 
    //                                     " Budget " + threadBudget );
    int         returnStatus;

    // Allocate a thread, then initialize it
    Thread threadCreated = new Thread(name);

    /*if (threadCreated == null) { // unsatisfiable -pcd
      System.out.println("Thread could not be created");
      returnStatus = threadMaximumThreadsExceeded;
    } else { */
    
    // Allocate stack and initialize the thread...
    if (!threadCreated.ConceptualObjectConstructor(periodIndex)) {
      threadCreated = null;
      returnStatus = threadInsufficientRAMForStack;
    } else {
      int interruptState = CPU.enterCritical();
      returnStatus = localStartThread(threadCreated, threadBudget, 
                                      periodIndex);
      CPU.exitCritical(interruptState);

      if (threadSuccess == returnStatus) {
      } else {
        threadCreated.ConceptualObjectDestructor();
        threadCreated = null;
      }
    }

    /*}*/

    return returnStatus;
  }

  static int deleteThreadK (Thread theThread) {
    //System.out.println(theThread + " Made it into deleteThread ");
    if (theThread != Scheduler.currentThread()) {
      System.out.println("Thread " + theThread + " no longer running delete");

      return 0;
    }

    int result;
    int interruptState = CPU.enterCritical();

    CPU.exitCritical(interruptState);
    theThread.initiateStopAndDelete();
    result = threadSuccess;
    interruptState = CPU.enterCritical();
    CPU.exitCritical(interruptState);

    return result;
  }

  static int localStartThread (Thread theThread, int requestedThreadBudget, 
                               int periodIndex) {
    // changed the followign code because can't pass int (budget) by reference.
    //  cpuTimeInMicroseconds budget; // budget set by following call.
    int budget; // budget set by following call.


    //System.out.println("DEOSKernel.localStartThread");
    budget = Scheduler.currentProcess().allocateCPUBudgetForThread(theThread, 
                                                 requestedThreadBudget, 
                                                 periodIndex);

    if (budget > -1) {
      theThread.startThread(budget);

      return threadSuccess;
    } else {
      return threadNotSchedulable;
    }
  }

  static int waitUntilNextPeriodK (Thread th) {
    //    System.out.println(th + " Made it into WaitUntil..." + Scheduler.currentThread());
    if (th != Scheduler.currentThread()) {
      System.out.println("Thread " + th + " no longer running");

      return 0;
    }


    //DEOS.handler.resetTimerInterrupt();
    Scheduler.currentThread().waitForNextPeriod();

    return 0; // void really
  }

  //} threadStatus;
}
