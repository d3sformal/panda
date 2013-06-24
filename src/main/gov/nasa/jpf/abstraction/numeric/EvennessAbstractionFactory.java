package gov.nasa.jpf.abstraction.numeric;

import java.util.List;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class EvennessAbstractionFactory extends AbstractionFactory {

	@Override
	public void tryAppendNew(List<Abstraction> abs_list, String[] args) {
		System.out.printf("### jpf-abstraction: EVENNESS turned on\n");
		
		abs_list.add(EvennessAbstraction.getInstance());
	}

}
