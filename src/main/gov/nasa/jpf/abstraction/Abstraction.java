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

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.numeric.SignsAbstraction;
import gov.nasa.jpf.abstraction.numeric.SignsValue;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.TruthValue;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

import gov.nasa.jpf.abstraction.common.BranchingCondition;
import gov.nasa.jpf.abstraction.common.BranchingConditionInfo;
import gov.nasa.jpf.abstraction.common.BranchingDecision;

/**
 * Common root class for numeric abstractions.
 */
public abstract class Abstraction {

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		throw new RuntimeException("get_domain_power not implemented");
	}		

	public AbstractValue abstractMap(int v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public AbstractValue abstractMap(float v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public AbstractValue abstractMap(long v) {
		throw new RuntimeException("abstract_map not implemented");
	}

	public AbstractValue abstractMap(double v) {
		throw new RuntimeException("abstract_map not implemented");
	}
	
    /**
     * Informs the abstraction about the start of a search (called once at the beginning)
     */
	public void start(ThreadInfo thread) {
	}
	
    /**
     * Informs the abstraction about an advancement
     */
	public void forward(MethodInfo method) {
	}
	
    /**
     * Informs the abstraction about a backtack
     * @param method Restored method
     */
	public void backtrack(MethodInfo method) {
	}
	
    /**
     * Informs the abstraction about a symbolic assignment
     * @param to An access expression referring to a primitive value
     */
	public void processPrimitiveStore(Expression from, AccessExpression to) {
	}
	
    /**
     * Informs the abstraction about a symbolic assignment
     * @param to An access expression referring to an object on heap
     */
	public void processObjectStore(Expression from, AccessExpression to) {
	}
	
    /**
     * Called by all InvokeInstructions to inform about a successful method invocations
     * @param before Caller stack
     * @param after  Callee stack
     */
	public void processMethodCall(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
	}
	
    /**
     * Called by all ReturnInstructions to inform about a successful return from a method
     * @param before Callee stack
     * @param after  Caller stack
     */
	public void processMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
	}
	
    /**
     * Called by all ReturnInstructions to inform about a successful return from a method
     * No return value
     * @param before Callee stack
     * @param after  Caller stack
     */
	public void processVoidMethodReturn(ThreadInfo threadInfo, StackFrame before, StackFrame after) {
	}
	
    /**
     * Most abstractions do not provide this behaviour, but branching instructions need this method to be present.
     */
	public BranchingConditionInfo processBranchingCondition(BranchingCondition condition) {
		return BranchingConditionInfo.NONE;
	}

    /**
     * Notify about a class that has not been CLINITed
     *
     * this is an alternative approach to let the abstraction know about its existence
     */
    public void processNewClass(ThreadInfo thread, ClassInfo classInfo) {
    }

    public void informAboutPrimitiveLocalVariable(Root root) {
    }

    public void informAboutStructuredLocalVariable(Root root) {
    }
	
	public void informAboutBranchingDecision(BranchingDecision decision) {
	}

    public void addThread(ThreadInfo threadInfo) {
    }

    public void scheduleThread(ThreadInfo threadInfo) {
    }

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(double)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._plus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._plus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._plus(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(float)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._plus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._plus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._plus(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(int)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._plus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._plus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._plus(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 + abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of sum.
	 * @see	#abstractMap(long)
	 * @see	#_plus(AbstractValue)
	 */
	public static AbstractValue _add(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._plus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._plus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._plus(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 & abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise conjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_and(AbstractValue)
	 */
	public static AbstractValue _and(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_and(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_and(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_and(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 & abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise conjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_and(AbstractValue)
	 */
	public static AbstractValue _and(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_and(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_and(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_and(abs_v1, v2);
		return result;
	}

	/**
	 * Compares two long integers, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(long)
	 * @see	#_cmp(AbstractValue)
	 */	
	public static SignsValue _cmp(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		SignsValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._cmp(abs_v2, abs_v1);
			else
				result = abs_v2.abs._cmp(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._cmp_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Compares two doubles, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(double)
	 * @see	#_cmp(AbstractValue)
	 */		
	public static SignsValue _cmpg(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		SignsValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._cmpg(abs_v2, abs_v1);
			else
				result = abs_v2.abs._cmpg(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._cmpg_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Compares two floats, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(float)
	 * @see	#_cmp(AbstractValue)
	 */		
	public static SignsValue _cmpg(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		SignsValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._cmpg(abs_v2, abs_v1);
			else
				result = abs_v2.abs._cmpg(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._cmpg_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Compares two doubles, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(double)
	 * @see	#_cmp(AbstractValue)
	 */			
	public static SignsValue _cmpl(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		SignsValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._cmpl(abs_v2, abs_v1);
			else
				result = abs_v2.abs._cmpl(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._cmpl_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Compares two floats, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of comparison.
	 * @see	#abstractMap(float)
	 * @see	#_cmp(AbstractValue)
	 */			
	public static SignsValue _cmpl(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		SignsValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._cmpl(abs_v2, abs_v1);
			else
				result = abs_v2.abs._cmpl(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._cmpl_reverse(abs_v1, v2);
		return result;
	}
	
	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(double)
	 * @see	#_div(AbstractValue)
	 */
	public static AbstractValue _div(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._div(abs_v2, abs_v1);
			else
				result = abs_v2.abs._div(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._div_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(float)
	 * @see	#_div(AbstractValue)
	 */	
	public static AbstractValue _div(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._div(abs_v2, abs_v1);
			else
				result = abs_v2.abs._div(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._div_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(int)
	 * @see	#_div(AbstractValue)
	 */		
	public static AbstractValue _div(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._div(abs_v2, abs_v1);
			else
				result = abs_v2.abs._div(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._div_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 / abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of division.
	 * @see	#abstractMap(long)
	 * @see	#_div(AbstractValue)
	 */		
	public static AbstractValue _div(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._div(abs_v2, abs_v1);
			else
				result = abs_v2.abs._div(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._div_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 == abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_eq(AbstractValue)
	 */			
	public static AbstractBoolean _eq(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._eq(abs_v2, abs_v1);
			else
				result = abs_v2.abs._eq(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._eq(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 >= abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_ge(AbstractValue)
	 */				
	public static AbstractBoolean _ge(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._ge(abs_v2, abs_v1);
			else
				result = abs_v2.abs._ge(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._ge_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 > abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_gt(AbstractValue)
	 */				
	public static AbstractBoolean _gt(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._gt(abs_v2, abs_v1);
			else
				result = abs_v2.abs._gt(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._gt_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 <= abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_le(AbstractValue)
	 */				
	public static AbstractBoolean _le(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._le(abs_v2, abs_v1);
			else
				result = abs_v2.abs._le(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._le_reverse(abs_v1, v2);
		return result;
	}
	
	/**
	 * Computes abs_v2 < abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_lt(AbstractValue)
	 */			
	public static AbstractBoolean _lt(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._lt(abs_v2, abs_v1);
			else
				result = abs_v2.abs._lt(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._lt_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(double)
	 * @see	#_mul(AbstractValue)
	 */				
	public static AbstractValue _mul(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._mul(abs_v2, abs_v1);
			else
				result = abs_v2.abs._mul(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._mul(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(float)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._mul(abs_v2, abs_v1);
			else
				result = abs_v2.abs._mul(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._mul(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(int)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._mul(abs_v2, abs_v1);
			else
				result = abs_v2.abs._mul(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._mul(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 * abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of multiplication.
	 * @see	#abstractMap(long)
	 * @see	#_mul(AbstractValue)
	 */			
	public static AbstractValue _mul(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._mul(abs_v2, abs_v1);
			else
				result = abs_v2.abs._mul(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._mul(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 != abs_v1, making calls to abstract_map
	 * before actual comparison if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract boolean value of comparison.
	 * @see	#abstractMap(int)
	 * @see	#_lt(AbstractValue)
	 */			
	public static AbstractBoolean _ne(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractBoolean result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._ne(abs_v2, abs_v1);
			else
				result = abs_v2.abs._ne(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._ne(abs_v1, v2);
		return result;
	}

	/**
	 * 
	 * @param abs_v	An abstract value
	 * @return A negation of abs_v
	 * @see	#_neg()
	 */			
	public static AbstractValue _neg(AbstractValue abs_v) {
		if (abs_v != null)
			return abs_v.abs._neg_impl(abs_v);
		else
			return null;
	}

	/**
	 * Computes abs_v2 | abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise disjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_or(AbstractValue)
	 */			
	public static AbstractValue _or(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_or(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_or(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_or(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 | abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise disjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_or(AbstractValue)
	 */				
	public static AbstractValue _or(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_or(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_or(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_or(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(double)
	 * @see	#_rem(AbstractValue)
	 */				
	public static AbstractValue _rem(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._rem(abs_v2, abs_v1);
			else
				result = abs_v2.abs._rem(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._rem_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(float)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._rem(abs_v2, abs_v1);
			else
				result = abs_v2.abs._rem(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._rem_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(int)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._rem(abs_v2, abs_v1);
			else
				result = abs_v2.abs._rem(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._rem_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 % abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of remainder.
	 * @see	#abstractMap(long)
	 * @see	#_rem(AbstractValue)
	 */					
	public static AbstractValue _rem(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._rem(abs_v2, abs_v1);
			else
				result = abs_v2.abs._rem(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._rem_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 << abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shl(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._shift_left(abs_v2, abs_v1);
			else
				result = abs_v2.abs._shift_left(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._shift_left_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 << abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shl(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._shift_left(abs_v2, abs_v1);
			else
				result = abs_v2.abs._shift_left(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._shift_left_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 >> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shr(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._shift_right(abs_v2, abs_v1);
			else
				result = abs_v2.abs._shift_right(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._shift_right_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 >> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _shr(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._shift_right(abs_v2, abs_v1);
			else
				result = abs_v2.abs._shift_right(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._shift_right_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(double)
	 * @see	#_sub(AbstractValue)
	 */					
	public static AbstractValue _sub(double v1, AbstractValue abs_v1, double v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._minus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._minus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._minus_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(float)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(float v1, AbstractValue abs_v1, float v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._minus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._minus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._minus_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(int)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._minus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._minus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._minus_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2-abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of subtraction.
	 * @see	#abstractMap(long)
	 * @see	#_sub(AbstractValue)
	 */				
	public static AbstractValue _sub(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._minus(abs_v2, abs_v1);
			else
				result = abs_v2.abs._minus(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._minus_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 >>> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(int)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _ushr(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._unsigned_shift_right(abs_v2, abs_v1);
			else
				result = abs_v2.abs._unsigned_shift_right(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._unsigned_shift_right_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 >>> abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of shifting.
	 * @see	#abstractMap(long)
	 * @see	#_shift_left(AbstractValue)
	 */					
	public static AbstractValue _ushr(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._unsigned_shift_right(abs_v2, abs_v1);
			else
				result = abs_v2.abs._unsigned_shift_right(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._unsigned_shift_right_reverse(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 ^ abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise exclusive disjunction.
	 * @see	#abstractMap(int)
	 * @see	#_bitwise_xor(AbstractValue)
	 */				
	public static AbstractValue _xor(int v1, AbstractValue abs_v1, int v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_xor(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_xor(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_xor(abs_v1, v2);
		return result;
	}

	/**
	 * Computes abs_v2 ^ abs_v1, making calls to abstract_map
	 * before actual computation if one of the AbstractValues is null. Should never
	 * be called with two nulls.
	 * 
	 * @param v1		A concrete value of the first operand
	 * @param abs_v1	An abstract value of the first operand
	 * @param v2		A concrete value of the second operand
	 * @param abs_v2	An abstract value of the second operand
	 * @return			The abstract value of bitwise exclusive disjunction.
	 * @see	#abstractMap(long)
	 * @see	#_bitwise_xor(AbstractValue)
	 */					
	public static AbstractValue _xor(long v1, AbstractValue abs_v1, long v2,
			AbstractValue abs_v2) {
		AbstractValue result = null;
		if (abs_v2 != null) {
			if (abs_v1 != null)
				result = abs_v2.abs._bitwise_xor(abs_v2, abs_v1);
			else
				result = abs_v2.abs._bitwise_xor(abs_v2, v1);
		} else if (abs_v1 != null)
			result = abs_v1.abs._bitwise_xor(abs_v1, v2);
		return result;
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */
	public SignsValue _cmp(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public SignsValue _cmp(AbstractValue left, long right) {
		return _cmp(left, left.abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	public SignsValue _cmpg(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public SignsValue _cmpg(AbstractValue left, float right) {
		return _cmpg(left, left.abs.abstractMap(right));
	}

	public SignsValue _cmpg(AbstractValue left, double right) {
		return _cmpg(left, left.abs.abstractMap(right));
	}

	/**
	 * @return Signs.ZERO if the operand is numerically equal to this
	 *         AbstractValue; Signs.NEG if this AbstractValue is numerically less
	 *         than the operand; and Signs.POS if this AbstractValue is
	 *         numerically greater than the operand.
	 */	
	public SignsValue _cmpl(AbstractValue left, AbstractValue right) {
		boolean n = false, z = false, p = false;
		if (_gt(left, right) != AbstractBoolean.FALSE)
			p = true;
		if (_lt(left, right) != AbstractBoolean.FALSE)
			n = true;
		if (_gt(left, right) != AbstractBoolean.TRUE
				&& _lt(left, right) != AbstractBoolean.TRUE)
			z = true;
		return SignsAbstraction.getInstance().create(n, z, p);
	}

	public SignsValue _cmpl(AbstractValue left, float right) {
		return _cmpl(left, left.abs.abstractMap(right));
	}

	public SignsValue _cmpl(AbstractValue left, double right) {
		return _cmpl(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_and(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("bitwise and not implemented");
	}

	public AbstractValue _bitwise_and(AbstractValue left, int right) {
		return _bitwise_and(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_and(AbstractValue left, long right) {
		return _bitwise_and(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_or(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("bitwise or not implemented");
	}

	public AbstractValue _bitwise_or(AbstractValue left, int right) {
		return _bitwise_or(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_or(AbstractValue left, long right) {
		return _bitwise_or(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_xor(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("bitwise xor not implemented");
	}

	public AbstractValue _bitwise_xor(AbstractValue left, int right) {
		return _bitwise_xor(left, left.abs.abstractMap(right));
	}

	public AbstractValue _bitwise_xor(AbstractValue left, long right) {
		return _bitwise_xor(left, left.abs.abstractMap(right));
	}

	protected final SignsValue _cmp_reverse(AbstractValue left, long right) {
		return _cmp(left.abs.abstractMap(right), left);
	}

	protected final SignsValue _cmpg_reverse(AbstractValue left, double right) {
		return _cmpg(left.abs.abstractMap(right), left);
	}

	protected final SignsValue _cmpg_reverse(AbstractValue left, float right) {
		return _cmpg(left.abs.abstractMap(right), left);
	}

	protected final SignsValue _cmpl_reverse(AbstractValue left, double right) {
		return _cmpl(left.abs.abstractMap(right), left);
	}

	protected final SignsValue _cmpl_reverse(AbstractValue left, float right) {
		return _cmpl(left.abs.abstractMap(right), left);
	}

	public AbstractValue _div(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("div not implemented");
	}

	public AbstractValue _div(AbstractValue left, double right) {
		return _div(left, left.abs.abstractMap(right));
	}

	public AbstractValue _div(AbstractValue left, float right) {
		return _div(left, left.abs.abstractMap(right));
	}

	public AbstractValue _div(AbstractValue left, int right) {
		return _div(left, left.abs.abstractMap(right));
	}

	public AbstractValue _div(AbstractValue left, long right) {
		return _div(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _div_reverse(AbstractValue left, double right) {
		return _div(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _div_reverse(AbstractValue left, float right) {
		return _div(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _div_reverse(AbstractValue left, int right) {
		return _div(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _div_reverse(AbstractValue left, long right) {
		return _div(left.abs.abstractMap(right), left);
	}

	public AbstractBoolean _eq(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("eq not implemented");
	}

	public AbstractBoolean _eq(AbstractValue left, int right) {
		return _eq(left, left.abs.abstractMap(right));
	}

	public AbstractBoolean _ge(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("ge not implemented");
	}

	public AbstractBoolean _ge(AbstractValue left, int right) {
		return _ge(left, left.abs.abstractMap(right));
	}

	protected final AbstractBoolean _ge_reverse(AbstractValue left, int right) {
		return _ge(left.abs.abstractMap(right), left);
	}

	public AbstractBoolean _gt(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("gt not implemented");
	}

	public AbstractBoolean _gt(AbstractValue left, int right) {
		return _gt(left, left.abs.abstractMap(right));
	}

	protected final AbstractBoolean _gt_reverse(AbstractValue left, int right) {
		return _gt(left.abs.abstractMap(right), left);
	}

	public AbstractBoolean _le(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("le not implemented");
	}

	public AbstractBoolean _le(AbstractValue left, int right) {
		return _le(left, left.abs.abstractMap(right));
	}

	protected final AbstractBoolean _le_reverse(AbstractValue left, int right) {
		return _le(left.abs.abstractMap(right), left);
	}

	public AbstractBoolean _lt(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("lt not implemented");
	}

	public AbstractBoolean _lt(AbstractValue left, int right) {
		return _lt(left, left.abs.abstractMap(right));
	}

	protected final AbstractBoolean _lt_reverse(AbstractValue left, int right) {
		return _lt(left.abs.abstractMap(right), left);
	}

	public AbstractValue _minus(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("minus not implemented");
	}

	public AbstractValue _minus(AbstractValue left, double right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _minus(AbstractValue left, float right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _minus(AbstractValue left, int right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _minus(AbstractValue left, long right) {
		return _minus(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _minus_reverse(AbstractValue left, double right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _minus_reverse(AbstractValue left, float right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _minus_reverse(AbstractValue left, int right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _minus_reverse(AbstractValue left, long right) {
		return _minus(left.abs.abstractMap(right), left);
	}

	public AbstractValue _mul(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("mul not implemented");
	}

	public AbstractValue _mul(AbstractValue left, double right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	public AbstractValue _mul(AbstractValue left, float right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	public AbstractValue _mul(AbstractValue left, int right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	public AbstractValue _mul(AbstractValue left, long right) {
		return _mul(left, left.abs.abstractMap(right));
	}

	public AbstractBoolean _ne(AbstractValue left, AbstractValue right) {
		return _eq(left, right).not();
	}

	public AbstractBoolean _ne(AbstractValue left, int right) {
		return _ne(left, left.abs.abstractMap(right));
	}

	public AbstractValue _neg_impl(AbstractValue abs) {
		throw new RuntimeException("negation not implemented");
	}

	public AbstractValue _plus(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("plus not implemented");
	}

	public AbstractValue _plus(AbstractValue left, double right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _plus(AbstractValue left, float right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	/**
	 * Computes the sum of this AbstractValue and abstract_map(right). It is a
	 * good idea to consider special cases when right is -1 or +1, because this
	 * can make increments and decrements more precise.
	 * 
	 * @return the result of adding operand to this AbstractValue
	 */
	public AbstractValue _plus(AbstractValue left, int right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _plus(AbstractValue left, long right) {
		return _plus(left, left.abs.abstractMap(right));
	}

	public AbstractValue _rem(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("rem not implemented");
	}

	public AbstractValue _rem(AbstractValue left, double right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	public AbstractValue _rem(AbstractValue left, float right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	public AbstractValue _rem(AbstractValue left, int right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	public AbstractValue _rem(AbstractValue left, long right) {
		return _rem(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _rem_reverse(AbstractValue left, double right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _rem_reverse(AbstractValue left, float right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _rem_reverse(AbstractValue left, int right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _rem_reverse(AbstractValue left, long right) {
		return _rem(left.abs.abstractMap(right), left);
	}

	public AbstractValue _shift_left(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("shift left not implemented");
	}

	public AbstractValue _shift_left(AbstractValue left, int right) {
		return _shift_left(left, left.abs.abstractMap(right));
	}

	public AbstractValue _shift_left(AbstractValue left, long right) {
		return _shift_left(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _shift_left_reverse(AbstractValue left, int right) {
		return _shift_left(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _shift_left_reverse(AbstractValue left, long right) {
		return _shift_left(left.abs.abstractMap(right), left);
	}

	public AbstractValue _shift_right(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _shift_right(AbstractValue left, int right) {
		return _shift_right(left, left.abs.abstractMap(right));
	}

	public AbstractValue _shift_right(AbstractValue left, long right) {
		return _shift_right(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _shift_right_reverse(AbstractValue left, int right) {
		return _shift_right(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _shift_right_reverse(AbstractValue left, long right) {
		return _shift_right(left.abs.abstractMap(right), left);
	}

	public AbstractValue _unsigned_shift_right(AbstractValue left, AbstractValue right) {
		throw new RuntimeException("shift right not implemented");
	}

	public AbstractValue _unsigned_shift_right(AbstractValue left, int right) {
		return _unsigned_shift_right(left, left.abs.abstractMap(right));
	}

	public AbstractValue _unsigned_shift_right(AbstractValue left, long right) {
		return _unsigned_shift_right(left, left.abs.abstractMap(right));
	}

	protected final AbstractValue _unsigned_shift_right_reverse(AbstractValue left, int right) {
		return _unsigned_shift_right(left.abs.abstractMap(right), left);
	}

	protected final AbstractValue _unsigned_shift_right_reverse(AbstractValue left, long right) {
		return _unsigned_shift_right(left.abs.abstractMap(right), left);
	}

	public boolean equals(AbstractValue left, AbstractValue right) {
		if (!getClass().getName().equals(right.getClass().getName()))
			throw new RuntimeException("### Error: Comparing different AbstractValues");
		return (getClass().getName().equals(right.getClass().getName()))
				&& (left.getKey() == right.getKey());
	}
}
