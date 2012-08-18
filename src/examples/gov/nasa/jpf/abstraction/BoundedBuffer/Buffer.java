package gov.nasa.jpf.abstraction.BoundedBuffer;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

public interface Buffer {
	public void put() throws InterruptedException; // put object into buffer

	public void get() throws InterruptedException; // get an object from buffer
}
