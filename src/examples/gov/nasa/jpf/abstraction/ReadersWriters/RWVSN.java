package gov.nasa.jpf.abstraction.ReadersWriters;

import java.util.Vector;

import gov.nasa.jpf.abstraction.Debug;

public class RWVSN {
	static RWPrinter rwp;

	public static void main(String argv[]) {
		rwp = new RWPrinter();

		new Writer(rwp).start();
		new Reader(rwp).start();
		new Writer(rwp).start();
		new Reader(rwp).start();
	}
}

final class Reader extends Thread {
	protected RWPrinter rwp;

	public Reader(RWPrinter r) {
		rwp = r;
	}

	public void run() {
		while (true)
			rwp.read();
	}
}

final class Writer extends Thread {
	protected RWPrinter rwp;

	public Writer(RWPrinter r) {
		rwp = r;
	}

	public void run() {
		while (true)
			rwp.write();
	}
}

class RWPrinter {
	int numWrite = 0;

	/**
	 * @assert PRE ReadMutex : numWrite==0;
	 */
	protected void read_() {
		assert (numWrite == 0);
		// System.out.println("reading");
	}

	protected void write_() {
		numWrite++;
		// System.out.println("writing");
		numWrite--;
	}

	protected int activeReaders_ = Debug.makeAbstractInteger(0); // counts
	protected int activeWriters_ = Debug.makeAbstractInteger(0);
	protected int waitingReaders_ = Debug.makeAbstractInteger(0);
	// the size of the waiting writers vector serves as its count

	// one monitor holds all waiting readers
	protected Object waitingReaderMonitor_ = this;

	// vector of monitors each holding one waiting writer
	protected Vector<Object> waitingWriterMonitors_ = new Vector<Object>();

	public void read() {
		beforeRead();
		read_();
		afterRead();
	}

	public void write() {
		beforeWrite();
		write_();
		afterWrite();
	}

	protected boolean allowReader() { // call under proper synch
		boolean result = activeWriters_ == 0
				|| waitingWriterMonitors_.size() == 0;
		return result;
		// error : change the && to an ||
	}

	protected boolean allowWriter() {
		boolean result = waitingWriterMonitors_.size() == 0
				&& activeReaders_ == 0 && activeWriters_ == 0;
		return result;
	}

	protected void beforeRead() {
		synchronized (waitingReaderMonitor_) {
			synchronized (this) { // test condition under synch
				if (allowReader()) {
					++activeReaders_;
					return;
				} else
					++waitingReaders_;
			}
			try {
				waitingReaderMonitor_.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	protected void beforeWrite() {
		Object monitor = new Object();
		synchronized (monitor) {
			synchronized (this) {
				if (allowWriter()) {
					++activeWriters_;
					return;
				}
				waitingWriterMonitors_.addElement(monitor); // append
			}
			try {
				monitor.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	protected synchronized void notifyReaders() { // waken readers
		synchronized (waitingReaderMonitor_) {
			waitingReaderMonitor_.notifyAll();
		}
		activeReaders_ = Debug.makeAbstractInteger(waitingReaders_); // all waiters now active
		waitingReaders_ = Debug.makeAbstractInteger(0);
	}

	protected synchronized void notifyWriter() { // waken 1 writer
		if (waitingWriterMonitors_.size() > 0) {
			Object oldest = waitingWriterMonitors_.firstElement();
			waitingWriterMonitors_.removeElementAt(0);
			synchronized (oldest) {
				oldest.notify();
			}
			++activeWriters_;
		}
	}

	protected synchronized void afterRead() {
		--activeReaders_;
		if (activeReaders_ == 0)
			notifyWriter();
	}

	protected synchronized void afterWrite() {
		--activeWriters_;
		if (waitingReaders_ > 0) // prefer waiting readers
			notifyReaders();
		else
			notifyWriter();
	}
}
