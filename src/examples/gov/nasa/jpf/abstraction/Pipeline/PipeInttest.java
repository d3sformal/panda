package gov.nasa.jpf.abstraction.Pipeline;

// "pipeline stop" precedes any "stage return" 
import gov.nasa.jpf.abstraction.Debug;

public class PipeInttest {
	static Pipeline pipe;
	static boolean stopCalled = false;

	public static void main(String[] argv) {
		pipe = new Pipeline(3);

		// abstract : i with signs
		for (int i = Debug.makeAbstractInteger(1); i < 2; i += 2) {
			pipe.add(i);
		}

		stopCalled = true;
		pipe.stop();
	}
}

class Pipeline {
	BlockingQueue first;

	Pipeline(int numStages) {
		BlockingQueue in, out;
		first = out = new BlockingQueue();
		for (int i = 0; i < numStages; i = i + 1) {
			in = out;
			out = new BlockingQueue();
			(new Stage(in, out)).start();
		}
		(new Listener(out)).start();
	}

	public void add(int o) {
		this.first.add(o);
	}

	public void stop() {
		this.first.stop();
	}
}

final class BlockingQueue {
	int queue = Debug.makeAbstractInteger(0);

	public final synchronized int take() {
		int value;

		while (this.queue < 1)
			try {
				this.wait();
			} catch (java.lang.InterruptedException ex) {
				;
			}

		value = this.queue;
		this.queue = Debug.makeAbstractInteger(0);
		return value;
	}

	public final synchronized void add(int o) {
		this.queue = Debug.makeAbstractInteger(o);
		this.notifyAll();
	}

	public final synchronized void stop() {
		this.queue = Debug.makeAbstractInteger(1);
		this.notifyAll();
	}
}

final class Stage extends java.lang.Thread {
	BlockingQueue input, output;

	public Stage(BlockingQueue in, BlockingQueue out) {
		this.input = in;
		this.output = out;
	}

	/**
	 * @assert POST ProperExit: PipeInttest.stopCalled;
	 */
	public void run() {
		int tmp = Debug.makeAbstractInteger(0);
		// while (tmp != 0) {
		// error : leaving out the negation
		while (tmp == 1) {
			tmp = Debug.makeAbstractInteger(this.input.take());
			if (tmp == 1)
				break;
			this.output.add(Debug.makeAbstractInteger(tmp + 2));
		}
		this.output.stop();
		assert (PipeInttest.stopCalled);
	}
}

final class Listener extends java.lang.Thread {
	BlockingQueue input;

	public Listener(BlockingQueue in) {
		this.input = in;
	}

	public void run() {
		int tmp = Debug.makeAbstractInteger(0);
		while (tmp != 1) {
			tmp = this.input.take();
			if (tmp == 1)
				break;
			System.out.println("output is " + tmp);
		}
	}
}