package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;

public class BaseTest {
    native public static void assertConjunction(String... assertions);

    public static void invokeOn(Class<?> cls) {
        /*
         @using=jpf-abstraction

        target=gov.nasa.jpf.abstraction.predicate.Test

        classpath=${jpf-abstraction}/build/tests
        sourcepath=${jpf-abstraction}/src/tests

        abstract.domain=PREDICATES ${jpf-abstraction}/src/tests/gov/nasa/jpf/abstraction/predicate/Test.pred

        listener=gov.nasa.jpf.abstraction.AbstractListener,gov.nasa.jpf.abstraction.util.InstructionTracker
        #,gov.nasa.jpf.abstraction.predicate.util.PredicateValuationMonitor,gov.nasa.jpf.abstraction.predicate.util.SMTMonitor
        #,gov.nasa.jpf.abstraction.util.InstructionTracker,gov.nasa.jpf.abstraction.predicate.util.SymbolTableMonitor

        #vm.serializer.class=gov.nasa.jpf.abstraction.predicate.PredicateAbstractionSerializer
        #search.multiple_errors=true
         */
        String[] args =  new String[] {};

        Config config = JPF.createConfig(args);

        JPF jpf = new JPF(config);

        jpf.run();
    }
}
