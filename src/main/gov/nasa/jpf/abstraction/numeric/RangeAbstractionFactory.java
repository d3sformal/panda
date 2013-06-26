package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;

public class RangeAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(String[] args) {
		try {
			int min = Integer.parseInt(args[1]);
			int max = Integer.parseInt(args[2]);

			System.out.printf("### jpf-abstraction: RANGE[%d, %d] turned on\n", min, max);

			return new RangeAbstraction(min, max);
		} catch (NumberFormatException nfe) {
			System.out.println("### jpf-abstraction: please keep format "
					+ "\"Interval MIN MAX\", where MIN and MAX are int");
		} catch (ArrayIndexOutOfBoundsException rce) {
			System.out.println("### jpf-abstraction: please keep format "
					+ "\"Interval MIN MAX\", where MIN and MAX are int");
		}
		
		return null;
	}

}
