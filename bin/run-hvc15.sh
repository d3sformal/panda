#!/bin/sh

CONFIG="+listener+=,gov.nasa.jpf.abstraction.util.TimeConstrainedJPF +jpf.time_limit=3600"

mkdir -p output

echo SAFE

bin/run.sh $CONFIG src/benchmarks/svcomp/loops/ArrayTrueUnreachableLabelPruning.jpf 2>&1 | tee output/array-pruning
echo
#bin/run.sh $CONFIG src/benchmarks/svcomp/loops/Eureka01TrueUnreachableLabelPruning.jpf 2>&1 | tee output/eureka-pruning
#echo
bin/run.sh $CONFIG src/benchmarks/svcomp/loops/TREX03TrueUnreachableLabelPruning.jpf 2>&1 | tee output/trex-pruning
echo
bin/run.sh $CONFIG src/benchmarks/svcomp/loops/InvertStringTrueUnreachableLabelPruning.jpf 2>&1 | tee output/invert-string-pruning
echo
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/PasswordPruning.jpf 2>&1 | tee output/password-pruning
echo
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/ReverseArrayPruning.jpf 2>&1 | tee output/reverse-array-pruning
echo
bin/run.sh $CONFIG src/benchmarks/svcomp/arrays/TwoIndicesPruning.jpf 2>&1 | tee output/two-indices-pruning
echo
#bin/run.sh $CONFIG src/benchmarks/dataflow/DataFlowAnalysisPruning.jpf 2>&1 | tee output/dataflow-pruning
#echo
bin/run.sh $CONFIG src/benchmarks/cycling/CyclingRacePruning.jpf 2>&1 | tee output/cycling-pruning
echo
#bin/run.sh $CONFIG src/benchmarks/image/ImagePruning.jpf 2>&1 | tee output/image-pruning
#echo
bin/run.sh $CONFIG src/benchmarks/scheduler/SchedulerPruning.jpf 2>&1 | tee output/scheduler-pruning
