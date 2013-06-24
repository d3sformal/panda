package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class EvennessAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		System.out.printf("### jpf-abstraction: EVENNESS turned on\n");
		
		return EvennessAbstraction.getInstance();
	}

}
