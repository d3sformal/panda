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
class PriorityListOfThreads {
  static final int numberOfThreadPriorities = 4; // from spin code 
  int              itsHighestPriorityMember;
  threadList[]     itsList;

  public PriorityListOfThreads () {
    //System.out.println("PriorityListOfThreads Constructor");
    itsHighestPriorityMember = 0;
    itsList = new threadList[numberOfThreadPriorities];

    for (int i = 0; i < numberOfThreadPriorities; i++) {
      itsList[i] = new threadList();
    }
  }

  public boolean isEmpty () {
    return highestPriorityMember().isEmpty();
  }

  public void addAtBeginning (threadListNode theNode) {
    //Verify.beginAtomic();
    //System.out.println("PriorityListOfThreads.addAtBeginning");
    int threadPriority = theNode.parent().currentPriority();
    itsList[threadPriority].addAtBeginning(theNode);

    if (itsHighestPriorityMember < threadPriority) {
      itsHighestPriorityMember = threadPriority;
    }

    //Verify.endAtomic();
  }

  public void addAtEnd (threadListNode theNode) {
    //Verify.beginAtomic();
    int threadPriority = theNode.parent().currentPriority();
    itsList[threadPriority].addAtEnd(theNode);

    if (itsHighestPriorityMember < threadPriority) {
      itsHighestPriorityMember = threadPriority;
    }

    //Verify.endAtomic();
  }

  public threadListNode head () {
    return highestPriorityMember().head();
  }

  public void mergeList (PriorityListOfThreads otherList) {
    //Verify.beginAtomic();
    threadList mine = itsList[0];
    threadList his = otherList.itsList[0];
    int        end = otherList.itsHighestPriorityMember + 1;
    int        i = 0;

    do {
      mine.mergeList(his);
      i++;
      mine = itsList[i];
      his = otherList.itsList[i];
    } while (i != end);

    if (itsHighestPriorityMember < otherList.itsHighestPriorityMember) {
      itsHighestPriorityMember = otherList.itsHighestPriorityMember;
    }

    otherList.itsHighestPriorityMember = 0;

    //Verify.endAtomic();
  }

  public void mergeList (threadList otherList) {
    //Verify.beginAtomic();
    //System.out.println("PriorityListOfThreads.mergeList");
    if (!otherList.isEmpty()) {
      int otherListPriority = otherList.head().parent().currentPriority();


      //System.out.println("otherListPriority = " + otherListPriority);
      //System.out.println("itsHighestPriorityMember = "
      //                  +itsHighestPriorityMember);
      itsList[otherListPriority].mergeList(otherList);

      if (itsHighestPriorityMember < otherListPriority) {
        itsHighestPriorityMember = otherListPriority;
      }
    }

    //Verify.endAtomic();
  }

  private threadList highestPriorityMember () {
    //Verify.beginAtomic();
    int hipri = itsHighestPriorityMember;

    for (; hipri > 0; hipri--) {
      if (!itsList[hipri].isEmpty()) {
        break;
      }
    }

    itsHighestPriorityMember = hipri;

    //System.out.println("HIPRI = " + hipri);
    //Verify.endAtomic();
    return itsList[hipri];
  }
  
  // remove unused import warning
  static Object V = Verify.class;
}
