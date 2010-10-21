//
//Copyright (C) 2007 United States Government as represented by the
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
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.abstraction.numeric.AbstractChoiceGenerator;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;

import gov.nasa.jpf.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class AbstractListener extends PropertyListenerAdapter implements PublisherExtension {


	Set<String> test_sequences = new HashSet<String>();; // here we print the test sequences
	private Map<String,MethodSummaryClean> allSummaries = new HashMap<String,MethodSummaryClean>();

	public AbstractListener(Config conf, JPF jpf) {
		jpf.addPublisherExtension(ConsolePublisher.class, this);
	}

	public void propertyViolated (Search search){
		System.out.println("--------->property violated");
		JVM vm = search.getVM();
		SystemState ss = vm.getSystemState();
		ChoiceGenerator cg = vm.getChoiceGenerator();
		if (!(cg instanceof AbstractChoiceGenerator)){
			ChoiceGenerator prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof AbstractChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		String error = search.getLastError().getDetails();
		error = "\"" + error.substring(0,error.indexOf("\n")) + "...\"";
	}

	public void instructionExecuted(JVM vm) {

			Instruction insn = vm.getLastInstruction();
			SystemState ss = vm.getSystemState();
			ThreadInfo ti = vm.getLastThreadInfo();
			Config conf = vm.getConfig();

			if (insn instanceof InvokeInstruction && insn.isCompleted(ti)) {
				InvokeInstruction md = (InvokeInstruction) insn;
				String methodName = md.getInvokedMethodName();
				Object [] args = md.getArgumentValues(ti);
				int numberOfArgs = args.length;
				MethodInfo mi = md.getInvokedMethod();
			}

			//else if (insn instanceof ReturnInstruction){
			// I don't think we need to do anything  here for printing test sequences...
			// but I'll put code for printing method summaries as in the original SymbolicListener
			//}


	}



	  public void stateBacktracked(Search search) {
		 // here do something similar to what you do when propertyViolated
	  }

      //	-------- the publisher interface
	  public void publishFinished (Publisher publisher) {
	    PrintWriter pw = publisher.getOut();
        // here just print the method sequences
	    publisher.publishTopicStart("Method Sequences");
	    Iterator<String> it = test_sequences.iterator();
	    while (it.hasNext())
	    	pw.println(it.next());
	  }

	  protected class MethodSummaryClean{
			private String methodName;
			private Object [] argValues;
			private Object [] symValues;
	  }
}
