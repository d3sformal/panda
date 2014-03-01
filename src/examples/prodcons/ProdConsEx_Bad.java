package prodcons;

public class ProdConsEx_Bad {
	public static final int DAILY_LIMIT = 1000000;

	public static void main(String[] args) {
		Data_Bad d = new Data_Bad();
		new Producer_Bad(d).start();
		new Consumer_Bad(d).start();
	}
}

class Data_Bad {
	public int value = 0;
	public boolean isNew = false;
}

class Producer_Bad extends Thread {
	private Data_Bad d;

	public Producer_Bad(Data_Bad d_) {
		super("Producer");
		this.d = d_;
	}

	public void run() {
		while (true) {
			// one iteration corresponds to one production day
			int remaining = ProdConsEx_Bad.DAILY_LIMIT;
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

class Consumer_Bad extends Thread {
	private Data_Bad d;

	public Consumer_Bad(Data_Bad d_) {
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
