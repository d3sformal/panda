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
public class NewTimer extends AbstractClockingDevice {
  int     startingTime = 0; // Time on the clock when timer was started
  int     setTime = 0; // Timer is set to this time
  int     stoppingTime = 0; // Time on clock when timer should stop
  boolean isStarted = false;
  boolean timeOut = false;
  boolean isInterrupted = false;

  /**
   * Primary constructor
   */
  public NewTimer () {
  }

  /**
   * To get the remaining time
   * @param currentTime, the current time on the clock
   * @return remainingTime, amount of time left
   */
  public int getRemainingTime (int currentTime) {
    //Verify.beginAtomic();
    int remainingTime = stoppingTime - currentTime;

    //System.out.println("Thread used: " + (currentTime - startingTime));
    // Assert: timeOut implies remainingTime = 0
    //assert (!timeOut || remainingTime == 0);
    //Verify.endAtomic();
    return remainingTime;
  }

  /**
   * To get the set-time
   */
  public int getSetTime () {
    return setTime;
  }

  /**
   * To get the stoppingTime
   * @return stoppingTime, the stopping time (on the clock) for the timer
   */
  public int getStoppingTime () {
    return stoppingTime;
  }

  /**
   * Time out
   * @param currentTime the current time on the clock
   * @return true if time out occurs
   */
  public boolean isTimeOut () {
    //assert (isStarted);
    if (timeOut) {
      isStarted = false;
    }

    return timeOut;
  }

  /**
   * To reset the timer
   * @param startingTimeIn value for setting timer
   */
  public void setTimer (int setTimeIn, int startingTimeIn) {
    //Verify.beginAtomic();
    //assert (setTimeIn > 0);
    //System.out.println("--- Resetting timer ---");
    //System.out.println("Starting time: " + startingTimeIn);
    startingTime = startingTimeIn;
    setTime = setTimeIn;
    stoppingTime = startingTime + setTime;


    //System.out.println("stopping time: " + stoppingTime);
    isStarted = true;
    isInterrupted = false;
    timeOut = false;

    //    Verify.endAtomic();
  }

  /**
   * Clear time out
   */
  public void clearTimeOut () {
    timeOut = false;
  }

  /**
   * When clock ticks, clock calls this method (inherited from
   * super class)
   */
  public void clockTicks (int currentTime) {
    if (stoppingTime == currentTime) {
      timeOut = true;
    }
  }

  /**
   * Interrupt timer
   */
  public void interruptTimer () {
    isStarted = false;
    isInterrupted = true;
  }
  
  // to remove unused import warning -pcd
  static Object V = Verify.class;
}
