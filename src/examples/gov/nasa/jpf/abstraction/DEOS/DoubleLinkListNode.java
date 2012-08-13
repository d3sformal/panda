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
class DoubleLinkListNode {
  protected DoubleLinkListNode previous;
  protected DoubleLinkListNode next;

  protected DoubleLinkListNode () {
    previous = null;
    next = null;
  }

  public boolean isOnAList () {
    /* $$A20 */
    return next != null;

    /* $$E20 */
  }

  public void addAfter (DoubleLinkListNode newNode) {
    // For some reason, the Borland 4.52 compiler was losing track of this
    // in previous versions of this inline code, but was able to handle it
    // if the code was not inlined.  This version seems to work OK.

    /* $$A60 */
    DoubleLinkListNode t = this;
    DoubleLinkListNode n = t.next;
    DoubleLinkListNode nn = newNode;
    nn.next = n;
    nn.previous = t;
    t.next = nn;
    n.previous = nn;

    /* $$E60 */
  }

  public void ensureNotOnList () {
    if (isOnAList()) {
      removeFromList();
    }
  }

  public DoubleLinkListNode nextNode () {
    return next;
  }

  public DoubleLinkListNode previousNode () {
    return previous;
  }

  public void removeFromList () {
    /* $$A10 */

    // Copy "this", previous and next to locals to help compiler realize they
    // won't change during the execution.  Eliminates redundant memory
    // references and copying.
    //System.out.println("DoubleLinkListNode.removeFromList");
    DoubleLinkListNode t = this;
    DoubleLinkListNode p = t.previous;
    DoubleLinkListNode n = t.next;
    n.previous = p;
    p.next = n;

    DoubleLinkListNode zero = null;
    t.previous = zero;
    t.next = zero;

    /* $$E10 */
  }
}
