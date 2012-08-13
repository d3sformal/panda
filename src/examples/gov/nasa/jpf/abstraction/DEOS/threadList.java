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
class threadList extends DoubleLinkListNode {
  protected threadList () {
    //System.out.println("threadList Constructor");
    previous = this;
    next = this;
  }

  public boolean isEmpty () {
    return next == this;
  }

  protected void addAtBeginning (threadListNode newNode) {
    addAfter(newNode);
  }

  protected void addAtEnd (threadListNode newNode) {
    //System.out.println("threadList.addAtEnd");
    previous.addAfter(newNode);
  }

  protected threadListNode head () {
    if (isEmpty()) {
      System.out.println("Attempt to acquire head of empty list!");
      System.exit(0);
    }

    return (threadListNode) next;
  }

  protected void mergeList (threadList otherList) {
    //System.out.println("threadList.mergeList");
    if (!otherList.isEmpty()) {
      previous.next = otherList.next;
      otherList.next.previous = previous;
      previous = otherList.previous;
      otherList.previous.next = this;
      otherList.next = otherList;
      otherList.previous = otherList;
    }
  }

  protected threadListNode tail () {
    if (isEmpty()) {
      System.out.println("Attempt to acquire tail of empty list!");
      System.exit(0);
    }

    return (threadListNode) previous;
  }
}
