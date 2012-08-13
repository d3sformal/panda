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
class Registry {
  static final int uSecsInFastestPeriod = 20;
  static final int uTicksInFastestPeriod = 1;
  static final int numPeriods = 3;

  static int periodDurationInMicroSecs (int periodIndex) {
    if (periodIndex == 0) {
      return uSecsInFastestPeriod;
    } else {
      return 3 * uSecsInFastestPeriod * periodIndex;
    }
  }

  static int periodDurationInSystemTicks (int periodIndex) {
    if (periodIndex == 0) {
      return uTicksInFastestPeriod;
    } else {
      return 3 * uTicksInFastestPeriod * periodIndex;
    }
  }
}
