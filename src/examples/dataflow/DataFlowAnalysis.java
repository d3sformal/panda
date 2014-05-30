package dataflow;

import gov.nasa.jpf.abstraction.Verifier;

public class DataFlowAnalysis
{
    public static void main(String[] args)
    {
        NodeInfo[] cfg = new NodeInfo[5];

        int[] succ;

        // initialize control flow graph

        succ = new int[1];
        succ[0] = 1;
        cfg[0] = new NodeInfo(succ);

        succ = new int[2];
        succ[0] = 2;
        succ[1] = 3;
        cfg[1] = new NodeInfo(succ);

        succ = new int[1];
        succ[0] = 4;
        cfg[2] = new NodeInfo(succ);

        succ = new int[1];
        succ[0] = 4;
        cfg[3] = new NodeInfo(succ);

        succ = new int[0];
        cfg[4] = new NodeInfo(succ);

        int i, j;

        // Each node can be placed in the queue as a successor of any other node -> at most n^2 elements
        int[] queue = new int[26]; // cfg.length * cfg.length + 1

        i = 0;
        j = i + 1;
        queue[i] = 0; // start

        int[] oldFacts = null;
        int[] newFacts = null;

        while (i != j)
        {
            int cfnodeID = queue[i];

            // we cannot use modulo
            i = i + 1;
            if (i >= queue.length) i = 0;

            oldFacts = cfg[cfnodeID].facts;

            // update facts and store in the newFacts variable
            newFacts = new int[oldFacts.length];
            for (int k = 0; k < oldFacts.length; ++k) {
                newFacts[k] = oldFacts[k];
            }

            newFacts[newFacts.length - 1] = Verifier.unknownInt();

            cfg[cfnodeID].facts = newFacts;

            boolean equal = (oldFacts[oldFacts.length - 1] == newFacts[newFacts.length - 1]);

            if ( ! equal )
            {
                // update queue based on CFG
                succ = cfg[cfnodeID].successors;

                for (int k = 0; k < succ.length; ++k) {
                    queue[j] = succ[k];

                    j = j + 1;
                    if (j >= queue.length) j = 0;
                }
            }
        }
    }


    static class NodeInfo
    {
        int[] successors;
        int[] facts;

        NodeInfo(int[] successors)
        {
            this.facts = new int[1];
            this.facts[0] = Verifier.unknownInt();
            this.successors = successors;
        }
    }
}
