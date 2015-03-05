package dataflow;

import gov.nasa.jpf.abstraction.Verifier;

public class DataFlowAnalysisSimple
{
    public static void main(String[] args)
    {
        NodeInfo[] cfg = new NodeInfo[1];

        int[] succ;

        // initialize control flow graph

        succ = new int[1];
        succ[0] = 1;
        cfg[0] = new NodeInfo(succ);

        int[] oldFacts = null;
        int[] newFacts = null;

        int cfnodeID = 0;

        oldFacts = cfg[cfnodeID].facts;

        newFacts = new int[oldFacts.length];
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
