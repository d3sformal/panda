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

import java.util.ArrayList;
import java.util.List;

/**
 * This abstraction is designed to combine other numeric abstractions.
 */
public class ContainerAbstraction extends Abstraction {

	private List<Abstraction> list = new ArrayList<Abstraction>();

	public ContainerAbstraction(List<Abstraction> lst) {
		list = lst;
	}
	
	/**
	 * Gets the list of abstract values, which describe a concrete value with
	 * abstractions specified by configuration. Abstract values are in the same
	 * order as specified. A null value inside the list means that some concrete
	 * value could not have been abstracted.
	 * 
	 * @return The list of abstract values.
	 */
	public List<Abstraction> getAbstractionsList() {
		return list;
	}

	/**
	 * 
	 * @return The number of abstract values in the domain.
	 */
    @Override
	public int getDomainSize() {
		int num = 1;
		for (int i = 0; i < list.size(); ++i)
			if (list.get(i) != null)
				num *= list.get(i).getDomainSize();
/*
			else {
				// get size of domain from global setup
				Abstraction abs = ((Container) AbstractInstructionFactory.abs)
						.getAbstractionsList().get(i);
				key *= abs.getDomainSize();
			}
*/
		return num;
	}
    
    public ContainerValue create(List<AbstractValue> lst) {
    	ContainerValue res = new ContainerValue(lst);
    	
    	res.abs = this;
    	
    	return res;
    }

	@Override
	public AbstractValue abstractMap(int v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(float v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(long v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

	@Override
	public AbstractValue abstractMap(double v) {
		ArrayList<AbstractValue> arr = new ArrayList<AbstractValue>();
		for (Abstraction abs : list) {
			AbstractValue elem = null;
			try {
				elem = abs.abstractMap(v);
			} catch (Exception nie) {
				System.out
						.println("### jpf-abstraction: abstract function failure for "
								+ abs.getClass() + " with " + v);
			}
			arr.add(elem);
		}
		return create(arr);
	}

}
