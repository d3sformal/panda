package gov.nasa.jpf.abstraction.predicate;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.List;
import java.util.LinkedList;

public class BaseTest {
    native public static void assertConjunction(String... assertions);

    native public static void assertDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertExclusiveDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertExclusiveDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertKnownValuation(String... assertions);

    native public static void assertAliased(String... aliases);
    native public static void assertNotAliased(String... aliases);

    native public static void assertNumberOfPossibleValues(String expression, int expectedNumber);

    protected List<String> config = new LinkedList<String>();

    public BaseTest() {
        // DO NOT USE multiple_errors
        // why: test driver would not skip calls to native assert methods
        config.add("+classpath=build/tests");
        config.add("+sourcepath=src/tests");
        config.add("+abstract.domain=PREDICATES src/tests/" + getClass().getName().replace(".", "/") + ".pred");
        config.add("+abstract.verbose=false");
        config.add("+listener=gov.nasa.jpf.abstraction.AbstractListener");
        config.add("+vm.serializer.class=gov.nasa.jpf.abstraction.predicate.PredicateAbstractionSerializer");
		config.add("+report.console.property_violation=error,snapshot");
        config.add("+target=" + getClass().getName());
    }

    private String[] getConfig() {
        return config.toArray(new String[config.size()]);
    }

    @Test
    public void bootstrap() {
        Config config = JPF.createConfig(getConfig());

        JPF jpf = new JPF(config);

        jpf.run();

        checkResult(jpf);
    }

    protected void checkResult(JPF jpf) {
        assertFalse(jpf.foundErrors());
    }
}
