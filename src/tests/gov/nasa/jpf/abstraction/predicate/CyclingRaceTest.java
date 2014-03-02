package gov.nasa.jpf.abstraction.predicate;

public class CyclingRaceTest extends BaseTest {
	public static void main(String[] args)
	{
		Cyclist[] cyclists = new Cyclist[3];
		
		Cyclist cl = null;
		
		// insert raw data for cyclists
		
		cl = new Cyclist();
		cl.idnum = 2;
		cl.time = 3725;
		cl.bonus = 5;
		cyclists[0] = cl;
		
		cl = new Cyclist();
		cl.idnum = 56;
		cl.time = 3569;
		cl.bonus = 10;
		cyclists[1] = cl;
		
		cl = new Cyclist();
		cl.idnum = 123;
		cl.time = 3766;
		cl.bonus = 50;
		cyclists[2] = cl;
		
		
		Cyclist[] results = new Cyclist[3];
		
		// compute results of the competition
        for (int i = 0; i < cyclists.length; ++i) {
			int res = cyclists[i].time - cyclists[i].bonus;

			int pos = 0;

            for (int j = i - 1; j >= 0; --j) 
			{
				if (res < results[j].time - results[j].bonus) results[j + 1] = results[j];
				else pos = j + 1;
            }

			results[pos] = cyclists[i];
		}

		assertDisjunction("results[0] = cyclists[0]: true", "results[0] = cyclists[1]: true", "results[0] = cyclists[2]: true");
		assertDisjunction("results[1].time = 3725: true", "results[1].time = 3569: true", "results[1].time = 3766: true");

        int[] diffs = new int[3];
		
		Cyclist bestCL = results[0];

		assertConjunction("bestCL.time = 3569: true", "bestCL.bonus = 10: true");

		int bestTime = bestCL.time - bestCL.bonus;
        diffs[0] = bestTime;
		for (int i = 1; i < results.length; ++i) {
			cl = results[i];
			int diff = cl.time - cl.bonus - bestTime;
            diffs[i] = diff;
		}
	}
}

class Cyclist 
{
	public int idnum;
	public int time;
	public int bonus;
}
