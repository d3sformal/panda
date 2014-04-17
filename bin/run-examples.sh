#!/bin/sh

BENCHMARKS=$(find src/examples -name "*.jpf" | sed -e 's:^src/examples/\(.*\)\.jpf$:\1:' -e 's:/:.:g')

. bin/run-statistics.sh
