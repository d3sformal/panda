package predsubsum2;


// small test for matching predicates based on abstract subsumption
// there should be different predicates on distinct execution paths

public class PredSubSum2
{
	public static void doIt() 
	{
		int v = 0;
		int t = 1;

		while (v >= 0)
		{
			// simulate modulo
			v = v + v + v + 1;
			while (v >= 10) v = v - 10;

			// simulate modulo
			t = t + t;
			while (t >= 3) t = t - 3;

			if (v >= 5)
			{
				if (t == 1)
				{
					v++;
				}

				t++;
			}
			else
			{
				t = t + 2;
				while (t >= 4) t = t - 4;

				if (t >= 2)
				{
					v += 2;
				}
			}
		}
	}


	public static void main(String[] args) 
	{
		PredSubSum2.doIt();
	}

}

