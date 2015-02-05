package gov.nasa.jpf.abstraction.util;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.VM;

/**
 * a listener to gracefully stop JPF after a specified amount of time 
 * and yet allow for the printing of statistics at the end of the search
 * use the property jpf.time_limit <time_in_seconds> to configure
 */
public class TimeConstrainedJPF extends ListenerAdapter {
	private long maxTime = 0;
	private long startTime = 0;
	
	public void searchStarted(Search search) {
		VM vm = search.getVM();
		Config config = search.getConfig();
		this.startTime = System.currentTimeMillis();
		this.maxTime = config.getInt("jpf.time_limit", -1);
		System.out.println("****TIME BOUNDED SEARCH - LIMIT SET TO (SECONDS): " + maxTime + " ****");
		this.maxTime = this.maxTime * 1000; //convert to milliseconds
	}
	
	public void stateAdvanced(Search search) {
		long duration = System.currentTimeMillis() - this.startTime;
		if (duration >= maxTime) {
			duration = duration / 1000;
			System.out.println("****TIME BOUNDED SEARCH - TOTAL TIME (SECONDS): " + duration + " ****");
			search.terminate();
		}
	}
}
