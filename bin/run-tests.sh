#!/bin/sh

cd src/tests

common_tests=`find gov/nasa/jpf/abstraction/common -name "*.java" | xargs | tr '/' '.'`
predabs_tests=`find gov/nasa/jpf/abstraction/predicate -name "*.java" | xargs | tr '/' '.'`

cd ../..

java_suffix=".java"

for t in $common_tests 
do
	echo "Running ${t%$java_suffix}"
	java -classpath ../jpf-core/build/jpf.jar:lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:build/classes:build/tests:build/main:lib/antlr-4.0-complete.jar org.junit.runner.JUnitCore ${t%$java_suffix}
done

for t in $predabs_tests 
do
	echo "Running ${t%$java_suffix}"
	java -classpath ../jpf-core/build/jpf.jar:lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:build/classes:build/tests:build/main:lib/antlr-4.0-complete.jar org.junit.runner.JUnitCore ${t%$java_suffix}
done

