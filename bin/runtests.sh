#!/bin/sh

java -classpath ../jpf-core/build/jpf.jar:lib/junit-4.11.jar:build/classes:build/tests:build/main:lib/antlr-4.0-complete.jar gov.nasa.jpf.abstraction.predicate.NullTestDriver
