package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;

import gov.nasa.jpf.Config;

public class IntervalAbstractionFactory extends AbstractionFactory {

	@Override
	public Abstraction create(Config config, String[] args) {
		try {
			double min = Double.parseDouble(args[1]);
			double max = Double.parseDouble(args[2]);

			System.out.printf("### jpf-abstraction: INTERVAL[%f, %f] turned on\n",
					min, max);
			
			return new IntervalAbstraction(min, max);
		} catch (NumberFormatException nfe) {
			System.out
					.println("### jpf-abstraction: please keep format "
							+ "\"Interval MIN MAX\", where MIN and MAX are doubles");
		} catch (ArrayIndexOutOfBoundsException rce) {
			System.out
					.println("### jpf-abstraction: please keep format "
							+ "\"Interval MIN MAX\", where MIN and MAX are doubles");
		}
		
		return null;
	}

}
