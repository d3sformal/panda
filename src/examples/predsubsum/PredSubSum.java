package predsubsum;


// small test for matching predicates based on abstract subsumption

public class PredSubSum
{
	public static final int MAX = 10;

	public static void doIt() 
	{
		int v = 0;

		while (true)
		{
			// simulate modulo
			v = v + v + v + 1;
			while (v >= MAX) v = v - MAX;

			if (v > 2 && v < MAX - 3)
			{
				v++;
				v++;
			}
		}
	}

	public static void main(String[] args) 
	{
		PredSubSum.doIt();
	}

}

