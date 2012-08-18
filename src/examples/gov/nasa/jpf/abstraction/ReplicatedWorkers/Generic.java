package gov.nasa.jpf.abstraction.ReplicatedWorkers;

public class Generic {
	public static void main(String arg[]) {
		ReplicatedWorkers theInstance;
		Configuration theConfig;
		GenericCollection workPool;
		GenericCollection resultPool;

		theConfig = new Configuration(Configuration.EXCLUSIVE,
				Configuration.SYNCHRONOUS, Configuration.SOMEVALUES);
		workPool = new GenericCollection();
		resultPool = new GenericCollection();

		theInstance = new ReplicatedWorkers(theConfig, workPool, resultPool, 4,
				1);
		theInstance.putWork();
		theInstance.execute();
		System.out.println("pool size " + theInstance.getPoolSize());

		assert (theInstance.GlobalDone || theInstance.getPoolSize() == 0);

		theInstance.destroy();
	}
}
