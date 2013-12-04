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
package gov.nasa.jpf.abstraction;

// does not work well for static methods:summary not printed for errors
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.abstraction.util.RunDetector;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

/**
 * AbstractListener monitors the state space traversal and individual instruction executions
 *
 * It informs the global abstraction about all the above mentioned events.
 */
public class AbstractListener extends PropertyListenerAdapter {

	@Override
	public void searchStarted(Search search) {
		RunDetector.initialiseNotRunning();
		GlobalAbstraction.getInstance().start(search.getVM().getCurrentThread().getTopFrameMethodInfo());
	}
	
	@Override
	public void stateAdvanced(Search search) {
        if (search.isVisitedState()) {
            System.out.println("VISITED STATE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            VM vm = search.getVM();
            	ThreadInfo ti = vm.getCurrentThread();
                    ti.printStackTrace();
        } else {
            System.out.println("NEW STATE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }

		RunDetector.advance();
		GlobalAbstraction.getInstance().forward(search.getVM().getCurrentThread().getTopFrameMethodInfo());
	}

	@Override
	public void stateBacktracked(Search search) {
		GlobalAbstraction.getInstance().backtrack(search.getVM().getCurrentThread().getTopFrameMethodInfo());
		RunDetector.backtrack();
	}
	
	@Override
	public void instructionExecuted(VM vm, ThreadInfo curTh, Instruction nextInsn, Instruction execInsn) {
		RunDetector.detectRunning(vm, nextInsn, execInsn);
	}
	
}
