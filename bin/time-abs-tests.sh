#!/bin/sh

cd src/tests

predabs_tests=`find gov/nasa/jpf/abstraction -mindepth 1 -maxdepth 1 -name "*.java" | sort | xargs | tr '/' '.'`

cd ../..

java_suffix=".java"

ACC=0

for t in $predabs_tests 
do
	exec 3>&1
	TIME=$( { time -p bin/run-test.sh -q ${t%$java_suffix} 1>&3; } 2>&1 )
	exec 3>&-
    TIME=`echo "$TIME" | sed -n 's/^real\s*//p'`
    ACC=`echo "$ACC + $TIME" | bc -l`
	echo "${t%$java_suffix}: ${TIME}s"
done

echo
echo "Total: ${ACC}s"
