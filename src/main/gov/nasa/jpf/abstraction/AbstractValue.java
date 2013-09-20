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

import java.util.Set;

/**
 * An abstract value is an element of an abstract domain (depending on abstraction in question)
 * 
 * e.g. abstract value associated with a concrete value -1 may be:
 * 
 * 1) NEGATIVE ... in case of signs abstraction
 * 2) ODD      ... in case of evenness abstraction
 * 
 * @see gov.nasa.jpf.abstraction.numeric for individual implementations
 */
public abstract class AbstractValue {
	protected int key;
	public Abstraction abs;

	/**
	 * This constructor is here to force all AbstractValues to call set_key. It is
	 * important, because keys are used to distinguish different abstract values
	 * during serialization.
	 * 
	 * @param key
	 *            An integer which bijectively defines a particular abstract
	 *            value
	 * @see #setKey
	 */
	protected AbstractValue(int key) {
		setKey(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	// returns the abstract token corresponding to the key
	public AbstractValue getToken(int key) {
		throw new RuntimeException("get_token not implemented");
	}

	/**
	 * 
	 * @return The set of possible abstract values.
	 */
	public Set<AbstractValue> getTokens() {
		throw new RuntimeException("get_tokens not implemented");
	}

	/**
	 * 
	 * @return The number of possible abstract values.
	 */
	public int getTokensNumber() {
		throw new RuntimeException("get_num_tokens not implemented");
	}
	
	/**
	 * @return true, if this abstraction is a single value from the domain;
	 * false, if this abstraction represents a set of values from the domain.
	 */
	public boolean isComposite() {
		return getTokensNumber() > 1;
	}
}
