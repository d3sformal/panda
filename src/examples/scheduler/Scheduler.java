package scheduler;

public class Scheduler
{
    private static int SCHEDULE_SIZE = 5;

	public static void main(String[] args) {
		ThreadInfo[] id2thread = new ThreadInfo[3];

        // prepare a few threads
        id2thread[0] = new ThreadInfo(5);
        id2thread[1] = new ThreadInfo(18);
        id2thread[2] = new ThreadInfo(10);


		// some threads are put into the active state
        id2thread[1].active = true;
        id2thread[2].active = true;

		// compute order in which threads should run
		int schedule_size = 0;
		int[] schedule = new int[SCHEDULE_SIZE];

		//int i = 0;
		//int j = 0;
		//int k = 0;
		
		// set of active threads is iterated when to make scheduling decisions
		for (int k = 0; k < id2thread.length; ++k) {
			ThreadInfo actTh = id2thread[k];

			if (!actTh.active) continue;

			if (schedule_size == 0)
			{
				schedule[0] = k;
				++schedule_size;
			}
			else
			{
				// the info object is retrieved for each active thread
				for (int i = 0; i < schedule_size; ++i) {
					ThreadInfo schTh = id2thread[schedule[i]];

					if (actTh.priority > schTh.priority) {
            	        // insert into the scheduling queue
                	    for (int j = schedule_size - 1; j >= i; --j) {
                    	    schedule[j + 1] = schedule[j];
	                    }
   						schedule[i] = k;
        	            ++schedule_size;
						break;
					}
				}
			}
		}
	}
}

class ThreadInfo
{
	public int priority;
    public boolean active;

	public ThreadInfo(int prio_) {
        active = false;
		priority = prio_;
	}
}
