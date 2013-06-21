package dataflow;

public class DataFlowAnalysis
{
	public static void main(String[] args)
	{
		NodeInfo[] cfg = new NodeInfo[5];

		// initialize control flow graph
		
		cfg[0] = new NodeInfo(1);
		cfg[1] = new NodeInfo(2, 3);
		cfg[2] = new NodeInfo(4);
		cfg[3] = new NodeInfo(4);
		cfg[4] = new NodeInfo();

		int i, j;
        // Each node can be placed in the queue as a successor of any other node -> at most n^2 elements
        int[] queue = new int[cfg.length * cfg.length + 1];

        i = 0;
        j = i + 1;
        queue[i] = 0; // start

		int[] oldFacts = null;
		int[] newFacts = null;

		while (i != j)
		{
			int cfnodeID = queue[i];
			i = (i + 1) % queue.length;
			
			oldFacts = cfg[cfnodeID].facts;

			// update facts and store in the newFacts variable
			newFacts = new int[oldFacts.length + 1];
			for (int k = 0; k < oldFacts.length; ++k) {
                newFacts[k] = oldFacts[k];
            }
			newFacts[newFacts.length - 1] = (queue.length + i - j) % queue.length;
			
			cfg[cfnodeID].facts = newFacts;

            boolean equal = (oldFacts.length == newFacts.length);

            for (int k = 0; equal && k < newFacts.length; ++k) {
                equal &= (newFacts[k] == oldFacts[k]);
            }

			if ( ! equal ) 
			{
				// update queue based on CFG
				int[] succ = cfg[cfnodeID].successors;

				for (int k = 0; k < succ.length; ++k) {
                    queue[j] = succ[k];
                    j = (j + 1) % queue.length;
                }
			}
		}
	}
}

class NodeInfo
{
  int[] successors;
  int[] facts;

  NodeInfo(int... successors) {
    this.facts = new int[0];
    this.successors = successors;
  }
}
