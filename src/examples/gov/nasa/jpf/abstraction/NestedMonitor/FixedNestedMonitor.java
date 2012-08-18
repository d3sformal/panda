package gov.nasa.jpf.abstraction.NestedMonitor;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

/********************* SEMABUFFER *****************************/

class SemaBufferFixed implements Buffer {
	// protected Object[] buf;
	// protected int in = 0;
	// protected int out= 0;
	// protected int count= 0;
	protected int size;

	Semaphore full; // counts number of items
	Semaphore empty;// counts number of spaces

	SemaBufferFixed(int size) {
		this.size = size;
		// buf = new Object[size];
		full = new Semaphore(0);
		empty = new Semaphore(size);
	}

	public void put(Object o) throws InterruptedException {
		empty.down();
		// buf[in] = o;
		// ++count;
		// in=(in+1) % size;
		full.up();
	}

	public Object get() throws InterruptedException {
		full.down();
		// Object o =buf[out];
		// buf[out]=null;
		// --count;
		// out=(out+1) % size;
		empty.up();
		return (null); // (o);
	}
}

public class FixedNestedMonitor {
	static int SIZE = 100; /* parameter */
	static Buffer buf;

	public static void main(String[] args) {
		buf = new SemaBufferFixed(SIZE);

		new ProducerFixed(buf).start();
		new ConsumerFixed(buf).start();
	}
}

/******************* PRODUCER ************************/
class ProducerFixed extends Thread {

	Buffer buf;

	ProducerFixed(Buffer b) {
		buf = b;
	}

	public void run() {
		try {
			// int tmp = 0;
			while (true) {
				buf.put(null); // (new Integer(tmp));
				// System.out.println(this + " produced " + tmp);
				// tmp=tmp+1;
			}
		} catch (InterruptedException e) {
		}
	}
}

/******************** CONSUMER *******************************/
class ConsumerFixed extends Thread {

	Buffer buf;

	ConsumerFixed(Buffer b) {
		buf = b;
	}

	public void run() {
		try {
			while (true) {
				buf.get(); // int tmp = ((Integer)buf.get()).intValue();
				// System.out.println(this+" consumed "+tmp);
			}
		} catch (InterruptedException e) {
		}
	}
}
