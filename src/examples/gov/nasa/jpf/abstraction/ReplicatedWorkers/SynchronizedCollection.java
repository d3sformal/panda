package gov.nasa.jpf.abstraction.ReplicatedWorkers;

final class SynchronizedCollection {
	private Collection theCollection;

	public SynchronizedCollection(Collection c) {
		theCollection = c;
	}

	public final synchronized int size() {
		return theCollection.size();
	}

	public final synchronized int take() {
		return theCollection.take();
	}

	public final synchronized void add() {
		theCollection.add();
	}
}
