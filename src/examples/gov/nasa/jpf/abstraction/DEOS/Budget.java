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
class Budget {
  private int itsTotalBudget;
  private int itsRemainingBudget;

  public void setRemainingBudgetInUsec (int b) {
    //System.out.println("Budget.setRemainingBudgetInUsec " + b);
    itsRemainingBudget = b;
  }

  public void setTotalBudgetInUsec (int b) {
    itsTotalBudget = b;
  }

  public void adjustRemainingTime (int b) {
    itsRemainingBudget += b;
  }

  public int remainingBudgetInUsec () {
    return itsRemainingBudget;
  }

  public void replenish () {
    //System.out.println("Budget.replenish");
    itsRemainingBudget = itsTotalBudget;
  }

  public void replenishAndStartTimer () {
    itsRemainingBudget = itsTotalBudget;
    startTimer();
  }

  public void startTimer () {
    //System.out.println("Budget.startTimer");
    // Modified by ckong - June 26, 2001
    // DEOS.theTimer.write(itsRemainingBudget);
    DEOS.systemClock.setTimer(itsRemainingBudget);
  }

  public int totalBudgetInUsec () {
    return itsTotalBudget;
  }

  int stopTimer () {
    // Modified by ckong - June 25, 2001
    //itsRemainingBudget = DEOS.theTimer.timeRemaining();
    itsRemainingBudget = DEOS.theTimer.getRemainingTime(
                               DEOS.systemClock.getCurrentTime());

    return itsRemainingBudget;
  }
}
