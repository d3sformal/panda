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
public class Clock {
  public static int TIME_CONSTRAINT = Registry.numPeriods * 20 * 2; // numPeriods is usually 3
  public static int NOINTERRUPTS = 0;
  public static int TIMEOUT = 1;
  public static int SYSTEMINTERRUPT = 2;
  public static int NOTIMECHANGE = 3;
  int               currentTime = -20;
  PeriodicClock     clockToNotify; // may use a list in another version
  NewTimer          timerToNotify; // if there are more than two clocking devices
  boolean           eventDriven = false;

  // For abstraction - need to keep track of current time before event fo

  /**
   * Main constructor
   */
  public Clock (PeriodicClock periodicIn, NewTimer timerIn) {
    if (DEOS.abstraction) {
      currentTime = -20;
    } else {
      currentTime = -1;
    }

    clockToNotify = periodicIn;
    timerToNotify = timerIn;
  }

  /**
   * To get the current time
   * @return currentTime
   */
  public int getCurrentTime () {
    return currentTime;
  }

  /**
   * To set the timer
   * @param timeIn time with which to set timer
   */
  public void setTimer (int timeIn) {
    timerToNotify.setTimer(timeIn, currentTime);
  }

  /**
   * Clock clears interrupts
   */
  public void clearInterrupts () {
    clockToNotify.clearInterrupt();
    timerToNotify.clearTimeOut();
  }

  /**
   * Clock ticks
   * @return int - NOINTERRUPTS or TIMEOUT or SYSTEMINTERRUPT
   */
  public int ticks () {
    clearInterrupts();

    int delta;

    if (!DEOS.abstraction) {
      delta = 1;
    } else {
      int timeToEOP = (clockToNotify.getTimeToEOP());
      int timeOutTime = (timerToNotify.getStoppingTime());
      int timeToTimeOut = (timeOutTime - currentTime);

      //System.out.println("currentTime = " + currentTime + " timeToEOP = " + timeToEOP + " timeOutTime = " + timeOutTime + " timeToTimeOut  = " + timeToTimeOut);
      if (Verify.randomBool()) {
        delta = 0;
      } else {
        if (timeToEOP <= timeToTimeOut) {
          delta = timeToEOP;
        } else {
          delta = timeToTimeOut;
        }
      }
    }

    if (delta == 0) {
      return NOTIMECHANGE;
    } else {
      if ((currentTime + delta) > TIME_CONSTRAINT) {
        return NOTIMECHANGE;
      }

      currentTime = (currentTime + delta);
      clockToNotify.clockTicks(currentTime);
      timerToNotify.clockTicks(currentTime);

      if (clockToNotify.isInterrupted()) {
        timerToNotify.interruptTimer();

        return SYSTEMINTERRUPT;
      } else if (timerToNotify.isTimeOut()) {
        return TIMEOUT;
      } else {
        return NOINTERRUPTS;
      }
    }
  }

  /**
   * System interrupt or time out events
   * @return the event that occurred
   */

  /* does not seem to be called anywhere?
     public int interruptEvents() {
       clearInterrupts();
       int timeToEOP = (clockToNotify.getTimeToEOP());
       int periodTime = (currentTime + timeToEOP);
       int timeOutTime = (timerToNotify.getStoppingTime());
       if (periodTime <= timeOutTime) {
         currentTime = periodTime;  
         clockToNotify.clockTicks(currentTime);
         timerToNotify.clockTicks(currentTime);
         return SYSTEMINTERRUPT;
       } else {
         currentTime = timeOutTime;
         clockToNotify.clockTicks(currentTime);
         timerToNotify.clockTicks(currentTime);
         return TIMEOUT;
       }
     }
   */
}
