package gov.nasa.jpf.abstraction.ReplicatedWorkers;

import gov.nasa.jpf.abstraction.Debug;
import gov.nasa.jpf.jvm.Verify;

class GenericCollection implements Collection {
	int count; // abstract with signs; zero_pos

	public final int size() {
		return count;
	}

	public final int take() {
		int tmp = Debug.makeAbstractInteger(0);
		if (count == 1) { // POS//if(count>0) {
			if (Verify.randomBool())// non-deterministic choice
				count = Debug.makeAbstractInteger(0);
			else
				count = Debug.makeAbstractInteger(1);// count = count - 1;
			tmp = Debug.makeAbstractInteger(1);
		}
		// System.out.println("take count "+count);
		return tmp;
	}

	public final void add() {
		count = Debug.makeAbstractInteger(1); // count = count + 1;
		// System.out.println("add count "+count);
	}

}
