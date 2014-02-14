package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;

import gov.nasa.jpf.Config;

public class SignsAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(Config config, String[] args) {
		System.out.printf("### jpf-abstraction: SIGNS turned on\n");

		return SignsAbstraction.getInstance();
	}

}
