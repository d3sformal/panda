#!/bin/sh

DIR=src/examples
BENCHMARKS=$(find ${DIR} -name "*.jpf" | sed -e 's:^'${DIR}'/\(.*\)\.jpf$:\1:' -e 's:/:.:g')

. bin/run-statistics.sh
