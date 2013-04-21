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
public class PeriodicClock {
  int     microSecsPeriod = 0;
  int     usedTime = 0;
  int     startingPeriodTime = 0;
  boolean systemInterrupt = false;
  int     special_case_usedTime = 0; // used to get correct value when systemtick can happen, but doesn't 

  /**
   * Main constructor
   * @param periodIn, the period of the clock - clock resets to zero
   * after period time elapsed
   */
  public PeriodicClock (int periodIn) {
    microSecsPeriod = periodIn;
  }

  /**
   * To get the tick value
   * @param currentTime the current time on the clock
   * @return true if tick
   */
  public boolean isInterrupted () {
    //assert (usedTime < microSecsPeriod);
    return systemInterrupt;
  }

  /**
   * To get the period
   * @return microSecsPeriod
   */
  public int getPeriod () {
    return microSecsPeriod;
  }

  /**
   * To get remaining time in period
   */
  public int getTimeToEOP () {
    return microSecsPeriod - usedTime;
  }

  /**
   * Clock event - abstraction when thread yields without interruption
   * @param usedTimeIn the current time on the clock
   */
  public void setUsedTime (int usedTimeIn) {
    usedTime = usedTimeIn;
  }

  /**
   * To get the time used in period
   * @param currentTime the current time on the clock
   */
  public int getUsedTime () {
    if (systemInterrupt) {
      return special_case_usedTime;
    }

    return usedTime;
  }

  /**
   * To clear interrupt
   */
  public void clearInterrupt () {
    systemInterrupt = false;
    resetUsedTime();
  }

  /**
   * Clock ticks - inherited from super class
   * @param currentTime the current time on the clock
   * @return void
   */
  public void clockTicks (int currentTime) {
    usedTime = currentTime % microSecsPeriod;

    //System.out.println("Clock - > usedtime = " + usedTime);
    if (usedTime == 0) {
      special_case_usedTime = microSecsPeriod;
      systemInterrupt = true;
      startingPeriodTime = currentTime;
    }
  }

  public void resetUsedTime () {
    special_case_usedTime = usedTime;
  }
  
  // remove unused import warning
  static Object V = Verify.class;
}
