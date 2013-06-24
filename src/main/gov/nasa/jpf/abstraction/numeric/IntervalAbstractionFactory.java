package gov.nasa.jpf.abstraction.numeric;

import java.util.List;

import gov.nasa.jpf.abstraction.AbstractionFactory;

public class IntervalAbstractionFactory extends AbstractionFactory {

	@Override
	public void tryAppendNew(List<Abstraction> abs_list, String[] args) {
		try {
			double min = Double.parseDouble(args[1]);
			double max = Double.parseDouble(args[2]);

			System.out.printf("### jpf-abstraction: INTERVAL[%f, %f] turned on\n",
					min, max);
			
			abs_list.add(new IntervalAbstraction(min, max));
		} catch (NumberFormatException nfe) {
			System.out
					.println("### jpf-abstraction: please keep format "
							+ "\"Interval MIN MAX\", where MIN and MAX are doubles");
		} catch (ArrayIndexOutOfBoundsException rce) {
			System.out
					.println("### jpf-abstraction: please keep format "
							+ "\"Interval MIN MAX\", where MIN and MAX are doubles");
		}
	}

}
