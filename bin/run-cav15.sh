#!/bin/sh

mkdir -p output

echo SAFE

bin/run.sh src/benchmarks/svcomp/loops/ArrayTrueUnreachableLabelAdjusting.jpf 2>&1 | tee output/array-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/ArrayTrueUnreachableLabelPruning.jpf 2>&1 | tee output/array-pruning
echo
bin/run.sh src/benchmarks/svcomp/loops/Eureka01TrueUnreachableLabelAdjusting.jpf 2>&1 | tee output/eureka-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/Eureka01TrueUnreachableLabelPruning.jpf 2>&1 | tee output/eureka-pruning
echo
bin/run.sh src/benchmarks/svcomp/loops/TREX03TrueUnreachableLabelAdjusting.jpf 2>&1 | tee output/trex-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/TREX03TrueUnreachableLabelPruning.jpf 2>&1 | tee output/trex-pruning
echo
bin/run.sh src/benchmarks/svcomp/loops/InvertStringTrueUnreachableLabelAdjusting.jpf 2>&1 | tee output/invert-string-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/InvertStringTrueUnreachableLabelPruning.jpf 2>&1 | tee output/invert-string-pruning
echo
bin/run.sh src/benchmarks/svcomp/arrays/PasswordAdjusting.jpf 2>&1 | tee output/password-adjusting
echo
bin/run.sh src/benchmarks/svcomp/arrays/PasswordPruning.jpf 2>&1 | tee output/password-pruning
echo
bin/run.sh src/benchmarks/svcomp/arrays/ReverseArrayAdjusting.jpf 2>&1 | tee output/reverse-array-adjusting
echo
bin/run.sh src/benchmarks/svcomp/arrays/ReverseArrayPruning.jpf 2>&1 | tee output/reverse-array-pruning
echo
bin/run.sh src/benchmarks/svcomp/arrays/TwoIndicesAdjusting.jpf 2>&1 | tee output/two-indices-adjusting
echo
bin/run.sh src/benchmarks/svcomp/arrays/TwoIndicesPruning.jpf 2>&1 | tee output/two-indices-pruning
echo
bin/run.sh src/benchmarks/dataflow/DataFlowAnalysisAdjusting.jpf 2>&1 | tee output/dataflow-adjusting
echo
bin/run.sh src/benchmarks/dataflow/DataFlowAnalysisPruning.jpf 2>&1 | tee output/dataflow-pruning
echo
bin/run.sh src/benchmarks/cycling/CyclingRaceAdjusting.jpf 2>&1 | tee output/cycling-adjusting
echo
bin/run.sh src/benchmarks/cycling/CyclingRacePruning.jpf 2>&1 | tee output/cycling-pruning
echo
bin/run.sh src/benchmarks/image/ImageAdjusting.jpf 2>&1 | tee output/image-adjusting
echo
bin/run.sh src/benchmarks/image/ImagePruning.jpf 2>&1 | tee output/image-pruning
echo
bin/run.sh src/benchmarks/scheduler/SchedulerAdjusting.jpf 2>&1 | tee output/scheduler-adjusting
echo
bin/run.sh src/benchmarks/scheduler/SchedulerPruning.jpf 2>&1 | tee output/scheduler-pruning
echo

echo UNSAFE

bin/run.sh src/benchmarks/svcomp/loops/ArrayFalseUnreachableLabelAdjusting.jpf 2>&1 | tee output/array-error-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/ArrayFalseUnreachableLabelPruning.jpf 2>&1 | tee output/array-error-pruning
echo
bin/run.sh src/benchmarks/svcomp/loops/Eureka01FalseUnreachableLabelAdjusting.jpf 2>&1 | tee output/eureka-error-adjusting
echo
bin/run.sh src/benchmarks/svcomp/loops/Eureka01FalseUnreachableLabelPruning.jpf 2>&1 | tee output/eureka-error-pruning
echo
