package gov.nasa.jpf.abstraction;

public class ProdConsExample_Bad {
	public static final int DAILY_LIMIT = 1000000;

	public static void main(String[] args) {
		Data_ d = new Data_();
		new Producer_(d).start();
		new Consumer_(d).start();
	}
}

class Data_ {
	public int value = 0;
	public boolean isNew = false;
}

class Producer_ extends Thread {
	private Data_ d;

	public Producer_(Data_ d_) {
		super("Producer");
		this.d = d_;
	}

	public void run() {
		while (true) {
			// one iteration corresponds to one production day
			int remaining = ProdConsExample_Bad.DAILY_LIMIT;
			while (remaining > 0) {
				synchronized (d) {
					d.value = 10; // dummy value
					d.isNew = true;
				}
				--remaining;
			}
		}
	}
}

class Consumer_ extends Thread {
	private Data_ d;

	public Consumer_(Data_ d_) {
		super("Consumer");
		this.d = d_;
	}

	public void run() {
		while (true) {
			synchronized (d) {
				if (d.isNew) {
					d.isNew = false;
					System.out.println(d.value);
				}
			}
		}
	}
}
