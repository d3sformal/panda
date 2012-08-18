package gov.nasa.jpf.abstraction.BoundedBuffer;

import gov.nasa.jpf.abstraction.Debug;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

/********************* BUFFER *****************************/

public class BufferImpl implements Buffer {

	// protected Object[] buf;
	// protected int in = 0;
	// protected int out= 0;
	protected int count = Debug.makeAbstractInteger(0);
	protected int size;

	public BufferImpl(int size) {
		this.size = size;
		// buf = new Object[size];
	}

	public synchronized void put() throws InterruptedException {
		while (count == size)
			wait();
		// buf[in] = o;
		++count;
		// in=(in+1) % size;
		notify();
	}

	public synchronized void get() throws InterruptedException {
		while (count == 0)
			wait();
		// Object o =buf[out];
		// buf[out]=null;
		--count;
		// out=(out+1) % size;
		notify();
		// return (o);
	}
}
