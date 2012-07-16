package gov.nasa.jpf.abstraction;


public class ProdConsExample
{
	public static final int DAILY_LIMIT = 1000000;

	public static void main(String[] args) 
	{
		Data d = new Data();
		new Producer(d).start();
		new Consumer(d).start();
	}
}

class Data
{
	public int value = 0;
	public boolean isNew = false;
}


class Producer extends Thread 
{
	private Data d;

	public Producer(Data d_) 
	{
		super("Producer");
		this.d = d_;
	}

	public void run() 
	{
		while (true) 
		{
			// one iteration corresponds to one production day
			int remaining = ProdConsExample.DAILY_LIMIT;
			while (remaining > 0) 
			{
				synchronized (d) 
				{
					d.value = 10; // dummy value
					d.isNew = true;
				}
				remaining--;
			}
		}
	}
}

class Consumer extends Thread 
{
	private Data d;

	public Consumer(Data d_) 
	{
		super("Consumer");
		this.d = d_;
	}

	public void run() 
	{
		while (true) 
		{
			synchronized (d) 
			{
				if (d.isNew) 
				{
					d.isNew = false;
					System.out.println(d.value);
				}
			}
		}
	}
}


