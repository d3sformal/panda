#!/bin/sh

BENCHMARKS="
    dataflow.DataFlowAnalysis
    cycling.CyclingRace
    image.Image
    scheduler.Scheduler

    svcomp.loops.ArrayTrueUnreachableLabel
    svcomp.loops.Eureka01TrueUnreachableLabel
    svcomp.loops.TREX03TrueUnreachableLabel
    svcomp.loops.InvertStringTrueUnreachableLabel
"

. bin/run-statistics.sh
