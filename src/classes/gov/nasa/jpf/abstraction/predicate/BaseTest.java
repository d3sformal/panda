package gov.nasa.jpf.abstraction.predicate;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

public class BaseTest {
    native public static void assertConjunction(String... assertions);

    native public static void assertDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertExclusiveDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertExclusiveDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertAliased(String... aliases);

    @Test
    public void bootstrap() {
        // CANNOT USE multiple_errors !!! NEVER EVER !!!
        // why: test driver would not skip calls to native assert methods
        String[] args =  new String[] {
            "+classpath=build/tests",
            "+abstract.domain=PREDICATES src/tests/" + getClass().getName().replace(".", "/") + ".pred",
            "+listener=gov.nasa.jpf.abstraction.AbstractListener,gov.nasa.jpf.abstraction.util.InstructionTracker",
            "+target=" + getClass().getName(),
        };

        Config config = JPF.createConfig(args);

        JPF jpf = new JPF(config);

        jpf.run();

        assertFalse(jpf.foundErrors());
    }
}
