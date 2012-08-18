package gov.nasa.jpf.abstraction.NestedMonitor;

public interface Buffer {
	public void put(Object o) throws InterruptedException; // put object into
															// buffer

	public Object get() throws InterruptedException; // get an object from
														// buffer
}
