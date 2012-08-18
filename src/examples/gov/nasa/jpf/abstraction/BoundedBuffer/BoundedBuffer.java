package gov.nasa.jpf.abstraction.BoundedBuffer;

import gov.nasa.jpf.abstraction.Debug;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

/**************************** MAIN **************************/

public class BoundedBuffer {
	static int SIZE = 1; /* parameter */
	static Buffer buf;

	public static void main(String[] args) {
		buf = new BufferImpl(SIZE);

		new Producer(buf).start();
		new Consumer(buf).start();
		new Producer(buf).start();
		new Consumer(buf).start();

		new Producer(buf).start();
		new Consumer(buf).start();
		new Producer(buf).start();
		new Consumer(buf).start();

	}
}
