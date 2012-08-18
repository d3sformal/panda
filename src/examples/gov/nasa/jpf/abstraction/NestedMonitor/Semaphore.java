package gov.nasa.jpf.abstraction.NestedMonitor;

import gov.nasa.jpf.abstraction.Debug;

public class Semaphore {

	private int value;

	public Semaphore(int initial) {
		value = Debug.makeAbstractInteger(initial);
	}

	synchronized public void up() {
		value = Debug.makeAbstractInteger(value);
		++value;
		notifyAll(); // should be notify() but does not work in some browsers
	}

	synchronized public void down() throws InterruptedException {
		value = Debug.makeAbstractInteger(value);		
		while (value == 0)
			wait();
		--value;
	}
}
