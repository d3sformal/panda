package gov.nasa.jpf.abstraction.numeric;

import java.util.List;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class RangeAbstractionFactory extends AbstractionFactory {

	@Override
	public void tryAppendNew(List<Abstraction> abs_list, String[] args) {
		try {
			int min = Integer.parseInt(args[1]);
			int max = Integer.parseInt(args[2]);

			System.out.printf("### jpf-abstraction: RANGE[%d, %d] turned on\n", min, max);

			abs_list.add(new RangeAbstraction(min, max));
		} catch (NumberFormatException nfe) {
			System.out.println("### jpf-abstraction: please keep format "
					+ "\"Interval MIN MAX\", where MIN and MAX are int");
		} catch (ArrayIndexOutOfBoundsException rce) {
			System.out.println("### jpf-abstraction: please keep format "
					+ "\"Interval MIN MAX\", where MIN and MAX are int");
		}
	}

}
