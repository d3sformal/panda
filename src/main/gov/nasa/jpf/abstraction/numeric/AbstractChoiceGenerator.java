//
// Copyright (C) 2007 United States Government as represented by the
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
package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.jvm.IntChoiceGenerator;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;


public class AbstractChoiceGenerator extends IntIntervalGenerator {

	boolean isReverseOrder;

	// assume we always have 2 choices: used only for bools in coditions
	public AbstractChoiceGenerator() {
		super("abstract",0,1,1);
		isReverseOrder = false;
	}

	/*
	 * If reverseOrder is true, the ChoiceGenerator
	 * explores paths in the opposite order used by
	 * the default constructor. If reverseOrder is false
	 * the usual behavior is used.
	 */
	public AbstractChoiceGenerator(boolean reverseOrder) {
		super("abstract",0, 1, reverseOrder ? -1 : 1);
		isReverseOrder = reverseOrder;
	}



	public IntChoiceGenerator randomize() {
		return new AbstractChoiceGenerator(random.nextBoolean());
	}

	public void setNextChoice(int nextChoice){
		super.next = nextChoice;
	}
}
