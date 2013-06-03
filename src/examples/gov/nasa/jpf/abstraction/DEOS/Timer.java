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
class Timer {
  static final int uSecsPeriod = Registry.uSecsInFastestPeriod;
  static int       Start_time = 0; // time 'requested' by thread and 'written to timer'
  static int       Remaining_time = 0;
  static int       Used_time = 0;

  // JAVA DEOS these indicate that this _could_ happen, not that it will 
  static boolean tick = false;
  static boolean timer = false;

  public Timer () {
  }

  public static void clearInterrupts () {
    timer = false;
    tick = false;
  }

  public int timeRemaining () {
    //Verify.beginAtomic();
    int used_in_period = 0; // how much time did thread use
    int time_to_eop = uSecsPeriod - Used_time; // time left in period

    // if tick and timer are still set, then you know they happended -
    // they are (should be) cleared by the threads otherwise
    if (tick) { // used all the time to eop OR no time
      used_in_period = time_to_eop;

      //System.out.println(" system tick interrupt");
    } else if (timer) { // used all the time OR no time
      used_in_period = Start_time;

      //System.out.println(" timer interrupt");
    } else if (time_to_eop <= Start_time) {
      DEOS.inc();

      if (!Verify.randomBool()) { // used all the time to eop OR no time
        used_in_period = time_to_eop;

        //DEOS.println("going to end of period");
      } else {
        used_in_period = 0;

        //DEOS.println("going no where");
      }
    } else { // time_to_eop > Start_time, i.e. use Start_time for calculations
      DEOS.inc();

      if (Verify.randomBool()) {
        used_in_period = Start_time;

        //DEOS.println("using full budget");
      } else {
        used_in_period = 0;

        //DEOS.println("using no budget");
      }
    }

    Used_time += used_in_period;

    if (tick) {
      Used_time = 0; // this is to help the invariant...
    }

    clearInterrupts();

    Remaining_time = Start_time - used_in_period;

    //DEOS.println("thread: " + used_in_period + " used, " +
    //				  Remaining_time + " remaining.  total period usage: " + Used_time);
    //		Verify.endAtomic();
    //assert (Remaining_time >= 0);
    //	}
    //System.out.println("Timer.timeRemaining " + Remaining_time);
    return Remaining_time;
  }

  public /*synchronized*/
   void write (int delayInMicroseconds) {
    Start_time = delayInMicroseconds;

    //DEOS.println("setting timer with " + Start_time);
    if ((Start_time + Used_time) >= uSecsPeriod) {
      tick = true; // tick may happen
    } else if ((Start_time + Used_time) < uSecsPeriod) {
      timer = true;
    } else {
      System.out.println("Timer ERROR - this case should not happen");

      //assert (true);
    }
  }
}
