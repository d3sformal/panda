#!/bin/sh

CONFIG="+listener+=,gov.nasa.jpf.abstraction.util.TimeConstrainedJPF +jpf.time_limit=3600"

bin/run.sh $CONFIG src/benchmarks/svcomp/loops/ArrayTrueUnreachableLabelAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/loops/Eureka01TrueUnreachableLabelAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/loops/TREX03TrueUnreachableLabelAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/loops/InvertStringTrueUnreachableLabelAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/PasswordAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/ReverseArrayAdjustingFullBacktrack.jpf
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/TwoIndicesAdjustingFullBacktrack.jpf
