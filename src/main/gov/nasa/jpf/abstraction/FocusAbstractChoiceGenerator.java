//
// Copyright (C) 2012 United States Government as represented by the
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
package gov.nasa.jpf.abstraction;

// import gov.nasa.jpf.vm.IntChoiceGenerator;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

// helps "focus" the analysis from Top to individual abstract tokens
// meant to be more precise

public class FocusAbstractChoiceGenerator extends IntIntervalGenerator {

	boolean isReverseOrder;

	// # choices = # elements in the abstract domain
	public FocusAbstractChoiceGenerator(int num_tokens) {
		super("focus", 0, num_tokens - 1, 1);
		isReverseOrder = false;
	}

	/*
	 * If reverseOrder is true, the ChoiceGenerator explores paths in the
	 * opposite order used by the default constructor. If reverseOrder is false
	 * the usual behavior is used.
	 */
	public FocusAbstractChoiceGenerator(int num_tokens, boolean reverseOrder) {
		super("focus", 0, num_tokens - 1, reverseOrder ? -1 : 1);
		isReverseOrder = reverseOrder;
	}

	// TODO
	// public IntChoiceGenerator randomize() {
	// return new FocusAbstractChoiceGenerator(random.nextBoolean());
	// }

	public void setNextChoice(int nextChoice) {
		super.next = nextChoice;
	}
}
