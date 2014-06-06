package gov.nasa.jpf.abstraction.predicate;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BaseTest {
    native public static void assertConjunction(String... assertions);

    native public static void assertDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertExclusiveDisjunction(String... assertions); // Each argument is a standalone fact
    native public static void assertExclusiveDisjunction(String[]... assertions); // Each argument is a set of standalone facts

    native public static void assertKnownValuation(String... assertions);

    native public static void assertAliased(String... aliases);
    native public static void assertNotAliased(String... aliases);
    native public static void assertMayBeAliased(String... accessExpressions);

    native public static void assertNumberOfPossibleValues(String expression, int expectedNumber);

    protected List<String> config = new LinkedList<String>();

    public BaseTest() {
        // DO NOT USE multiple_errors
        // why: test driver would not skip calls to native assert methods
        config.add("+classpath=build/tests");
        config.add("+sourcepath=src/tests");
        config.add("+apf.abstract_domain=PREDICATES src/tests/" + getClass().getName().replace(".", "/") + ".pred");
        config.add("+apf.verbose=false");
        config.add("+listener=gov.nasa.jpf.abstraction.AbstractListener");
        config.add("+vm.serializer.class=gov.nasa.jpf.abstraction.predicate.PredicateAbstractionSerializer");
        config.add("+report.console.property_violation=error,snapshot");
        config.add("+target=" + getClass().getName());
    }

    private String[] getConfig() {
        return config.toArray(new String[config.size()]);
    }

    public void enableBranchPruning() {
        config.add("+apf.branch.pruning=true");
    }

    public void disableStateMatching() {
        config.add("+vm.storage.class=");
    }

    @Test
    public void bootstrap() {
        Config config = JPF.createConfig(getConfig());

        Class<? extends BaseTest> cls = this.getClass();

        List<String> targetEntries = new LinkedList<String>();
        List<Boolean> targetShouldPass = new LinkedList<Boolean>();

        for (Method m : cls.getDeclaredMethods()) {
            Annotation t = m.getAnnotation(gov.nasa.jpf.abstraction.predicate.Test.class);
            boolean pass = true;

            if (t == null) {
                t = m.getAnnotation(gov.nasa.jpf.abstraction.predicate.FailingTest.class);
                pass = false;
            }

            if (t != null) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new RuntimeException("The test method `" + m.getName() + "` must be static.");
                }
                if (!m.getReturnType().equals(Void.TYPE)) {
                    throw new RuntimeException("The test method `" + m.getName() + "` must return void.");
                }

                Class<?>[] params = m.getParameterTypes();

                if (params.length == 0) {
                    targetEntries.add(m.getName() + "()V");
                } else if (params.length == 1) {
                    if (params[0].equals(String.class)) {
                        targetEntries.add(m.getName() + "(Ljava/lang/String;)V");
                    } else if (params[0].isArray() && params[0].getComponentType().equals(String.class)) {
                        targetEntries.add(m.getName() + "([Ljava/lang/String;)V");
                    } else {
                        throw new RuntimeException("The test method `" + m.getName() + "` takes a parameter of an unsupported type.");
                    }
                } else {
                    throw new RuntimeException("The test method `" + m.getName() + "` takes unsupported number of parameters.");
                }

                targetShouldPass.add(pass);
            }
        }

        if (targetEntries.isEmpty()) {
            targetEntries.add("main([Ljava/lang/String;)V");
            targetShouldPass.add(true);
        }

        boolean allPassed = true;

        Iterator<String> entryIter = targetEntries.iterator();
        Iterator<Boolean> passIter = targetShouldPass.iterator();

        while (entryIter.hasNext()) {
            String entry = entryIter.next();
            Boolean expectedPass = passIter.next();

            config.setTargetEntry(entry);

            JPF jpf = new JPF(config);

            jpf.run();

            allPassed = reducePassed(allPassed, expectedPass, checkPassed(jpf));
        }

        assertTrue(allPassed);
    }

    protected boolean reducePassed(boolean passedSoFar, boolean expectedNext, boolean passedNext) {
        return passedSoFar && (expectedNext == passedNext);
    }

    protected boolean checkPassed(JPF jpf) {
        return !jpf.foundErrors();
    }
}
