package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;

public class BaseTest {
    native public static void assertConjunction(String... assertions);

    public static void invokeOn(Class<?> cls) {
        /*
        classpath=${jpf-abstraction}/build/tests
        sourcepath=${jpf-abstraction}/src/tests
         */
        String[] args =  new String[] {
            "+classpath=build/tests",
            "+abstract.domain=PREDICATES src/tests/" + cls.getName().replace(".", "/") + ".pred",
            "+listener=gov.nasa.jpf.abstraction.AbstractListener,gov.nasa.jpf.abstraction.util.InstructionTracker",
            "+target=" + cls.getName(),
            
        };

        Config config = JPF.createConfig(args);

        JPF jpf = new JPF(config);

        jpf.run();
    }
}
