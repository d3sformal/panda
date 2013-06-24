package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class SignsAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		System.out.printf("### jpf-abstraction: SIGNS turned on\n");

		return SignsAbstraction.getInstance();
	}

}
