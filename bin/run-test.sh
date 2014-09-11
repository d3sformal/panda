#!/bin/sh

java -cp lib/jpf-core/build/jpf.jar:lib/junit-4.11.jar:lib/antlr-4.0-complete.jar:build/annotations/:build/classes/:build/main/:build/peers/:build/tests/ gov.nasa.jpf.abstraction.util.TestRunner $1
