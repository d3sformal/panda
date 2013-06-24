package gov.nasa.jpf.abstraction.numeric;

import java.util.List;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class SignsAbstractionFactory extends AbstractionFactory {

	@Override
	public void tryAppendNew(List<Abstraction> abs_list, String[] args) {
		System.out.printf("### jpf-abstraction: SIGNS turned on\n");

		abs_list.add(SignsAbstraction.getInstance());
	}

}
