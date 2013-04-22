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
package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.search.Search;

import gov.nasa.jpf.abstraction.numeric.FocusAbstractChoiceGenerator;


public class DebugMonitor extends ListenerAdapter 
{
	public DebugMonitor(Config cfg, JPF jpf)
	{
	}


	public void stateAdvanced(Search search)
	{
		System.out.print("[MONITOR] state : ");
		if (search.isNewState()) System.out.print("new");
		else System.out.print("visited");
		System.out.println(" , id = " + search.getStateId());
	}
	
	public void stateBacktracked(Search search)
	{
		System.out.println("[MONITOR] backtrack");
	}

	public void choiceGeneratorRegistered(VM vm) 
	{
		ChoiceGenerator<?> cg = vm.getChoiceGenerator(); // TODO: VERIFY THIS CONVERSION FROM 6 TO 7 (ORIGINALLY getLastChoiceGenerator())

		if (cg instanceof FocusAbstractChoiceGenerator) 
		{
			System.out.println("[MONITOR] new focus cg");
		}
	}

	public void choiceGeneratorAdvanced(VM vm) 
	{
		ChoiceGenerator<?> cg = vm.getChoiceGenerator(); // TODO: VERIFY THIS CONVERSION FROM 6 TO 7 (ORIGINALLY getLastChoiceGenerator())
	
		if (cg instanceof FocusAbstractChoiceGenerator) 
		{
			System.out.println("[MONITOR] focus cg : more choices = " + cg.hasMoreChoices() + ", current value = " + cg.getNextChoice());
		}
	} 
}	

