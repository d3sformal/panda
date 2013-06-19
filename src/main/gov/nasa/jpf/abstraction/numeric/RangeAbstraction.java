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
package gov.nasa.jpf.abstraction.numeric;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract domain for given two integer or floating-point values MIN and
 * MAX is the set of all integers in interval [MIN, MAX], and also LESS and GREATER
 * to express the fact that a value is less than MIN or greater than MAX,
 * respectively. This abstraction can be used for integer values only.
 * 
 * When the result of an abstract operation cannot be defined unambiguously
 * (e.g. LESS + LESS can be LESS, GREATER or any value between them), special
 * "composite tokens" which represent a set of abstract values are returned. They
 * are not represented by static members since their number increases exponentially
 * with value of (MAX-MIN).
 * 
 * Remember, that this abstraction does not handle such floating-point values as
 * NaN and INF.
 */
public class RangeAbstraction extends Abstraction {
	
	// the key for state matching is enumeration of { LESS, MIN, MIN+1, ..., MAX-1, MAX, GREATER }
	// e.g LESS.get_key() == 0, MIN.get_key() == 1, ..., MAX.get_key() == MAX-MIN+1, ...
	
	// since a composite abstract value will never be the result of any bytecode
	// and thus will never take part in the state matching, all such values
	// have -1 as their key.
	
	public int MIN = 0;
	public int MAX = 0;
	
	public RangeAbstraction(int min, int max) {
		MIN = min;
		MAX = max;
	}
	
	public RangeValue create(Set<Integer> values) {
		RangeValue res = new RangeValue(-1);

		if (values.size() == 0)
			throw new RuntimeException("Invalid value");		
		if (values.size() > 1) // isComposite
			res.setKey(-1);
		else
			for (Integer v : values)
				res.setKey(v-MIN+1);
		for (Integer v : values)
			res.values.add(v);
		
		res.abs = this;
		
		return res;
	}		

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
	public int getDomainSize() {
		return MAX-MIN+3;
	}	
	
	@Override
	public RangeValue abstractMap(int v) {
		if (v < MIN)
			v = MIN-1;
		else if (v > MAX)
			v = MAX+1;
		RangeValue res = new RangeValue(v-MIN+1);
		res.values.add(v);
		res.abs = this;
		return res;
	}
	
}
