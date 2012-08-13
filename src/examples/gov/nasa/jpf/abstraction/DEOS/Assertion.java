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

import gov.nasa.jpf.abstraction.Debug;

/**
 * DOCUMENT ME!
 */
class Assertion {
  // static List allThreads = new ArrayList();
  static Thread[] allThreads = new Thread[4];
  static int[]    total_time = new int[2];
  static int      num_entries = 0;

  public static void addThread (Thread t) {
    //allThreads.add(t);
    allThreads[num_entries] = t;
    num_entries++;
  }

  public static boolean check () {
    //Verify.beginAtomic();
    total_time[0] = Debug.makeAbstractInteger(0);
    total_time[1] = Debug.makeAbstractInteger(0);

    //Iterator it = allThreads.iterator();
    Thread current;

    //while(it.hasNext()) {
    for (int i = 0; i < num_entries; i++) {
      //current = (Thread)it.next();
      current = allThreads[i];

      if (current != Scheduler.idleThread()) {
        int cp = current.itsPeriodicEvent.currentPeriod();

        if ((current.itsLastExecution == cp) && 
                (current.itsLastCompletion != cp)) {
          //System.out.println(" " + current + " executed current period:");
          total_time[1] += current.itsCurrentBudget.remainingBudgetInUsec();

          //System.out.println("then: " + current + 
          // " adds " + current.itsCurrentBudget.remainingBudgetInUsec() + 
          // " total = " + total_time[1]);
          if (current.itsPeriodIndex == 0) {
            total_time[0] += current.itsCurrentBudget.remainingBudgetInUsec();
          }
        } else if (current.itsLastExecution != cp) {
          total_time[1] += current.itsCurrentBudget.totalBudgetInUsec();

          //System.out.println("else: " + current + 
          //" adds " + current.itsCurrentBudget.totalBudgetInUsec() + 
          //" total = " + total_time[1]);
          if (current.itsPeriodIndex == 0) {
            total_time[0] += current.itsCurrentBudget.totalBudgetInUsec();
          }
        }

        if (current.itsPeriodIndex == 0) {
          int tmp = (current.itsCurrentBudget.totalBudgetInUsec()) * (StartOfPeriodEvent.eventForPeriodIndex(1).countDown - 1);
          total_time[1] += tmp;

          //System.out.println(current + 
          //       " adds future " + tmp + " total = " + total_time[1]);
        }
      }
    }

    // calculate time remaining in period
    int period_count = StartOfPeriodEvent.eventForPeriodIndex(1).countDown - 1;

    //System.out.println("period count = " + period_count);
    /*int current_period = StartOfPeriodEvent.eventForPeriodIndex(1)
                                           .currentPeriod();*/

    //System.out.println("current period = " + current_period);
    
    // Modified by ckong - June 26, 2001
    //int remaining = (Registry.uSecsInFastestPeriod*period_count) +
    //  Registry.uSecsInFastestPeriod - Timer.Used_time; 
    if (Scheduler.currentThread() != Scheduler.idleThread()) {
      int remaining = ((Registry.uSecsInFastestPeriod * period_count) + 
                      Registry.uSecsInFastestPeriod) - 
                      DEOS.thePeriodicClock.getUsedTime();
      remaining = Debug.makeAbstractInteger(remaining);

      //System.out.println("remaining: " + remaining);
      // THE ACTUAL ASSERTION!
      if (total_time[1] > remaining) {
        DEOS.println("Ooops: Time wanted " + total_time[1] + " > " + 
                     remaining);
        assert false;
      } else {
        //System.out.println("Fine: wanted " + total_time[1] + " <= " + remaining);
      }
    }

    total_time[0] = Debug.makeAbstractInteger(0);
    total_time[1] = Debug.makeAbstractInteger(0);

    return true;
  }

  public static void removeThread (Thread t) {
    //allThreads.remove(t);
    for (int i = 0; i < num_entries; i++) {
      if (allThreads[i] == t) {
        for (int j = i + 1; j < num_entries; j++) {
          allThreads[j - 1] = allThreads[j];
        }

        num_entries--;

        return;
      }
    }
  }
}
