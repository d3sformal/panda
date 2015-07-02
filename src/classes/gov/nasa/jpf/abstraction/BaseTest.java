package gov.nasa.jpf.abstraction;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.nasa.jpf.JPF;

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

    native public static void addMethodAbstractionPredicate(String p);
    native public static void addObjectAbstractionPredicate(String p);
    native public static void addStaticAbstractionPredicate(String p);

    protected List<String> config = new LinkedList<String>();

    public BaseTest() {
        // DO NOT USE multiple_errors
        // why: test driver would not skip calls to native assert methods
        config.add("+classpath=build/tests");
        config.add("+sourcepath=src/tests");

        String filename = "src/tests/" + getClass().getName().replace(".", "/") + ".pred";

        if (new File(filename).isFile()) {
            config.add("+panda.abstract_domain=PREDICATES " + filename);
        } else {
            config.add("+panda.abstract_domain=PREDICATES");
        }

        config.add("+panda.verbose=false");
        config.add("+listener=gov.nasa.jpf.abstraction.AbstractListener");
        config.add("+vm.serializer.class=gov.nasa.jpf.abstraction.PredicateAbstractionSerializer");
        config.add("+report.console.property_violation=error,snapshot");
        config.add("+target=" + getClass().getName());
    }

    private String[] getConfig(String... targetConfig) {
        String[] config = this.config.toArray(new String[this.config.size() + targetConfig.length]);

        for (int i = 0; i < targetConfig.length; ++i) {
            config[this.config.size() + i] = targetConfig[i];
        }

        return config;
    }

    public void enableBranchPruning() {
        config.add("+panda.branch.prune_infeasible=true");
    }

    public void disableStateMatching() {
        config.add("+vm.storage.class=");
    }

    @Test
    public void bootstrap() {
        Class<? extends BaseTest> cls = this.getClass();

        List<String> targetEntries = new LinkedList<String>();
        List<String[]> targetConfig = new LinkedList<String[]>();
        List<Boolean> targetShouldPass = new LinkedList<Boolean>();

        SortedSet<Method> methods = new TreeSet<Method>(new Comparator<Method>() {
            @Override
            public int compare(Method m1, Method m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });

        for (Method m : cls.getDeclaredMethods()) {
            methods.add(m);
        }

        for (Method m : methods) {
            Annotation t = m.getAnnotation(gov.nasa.jpf.abstraction.Test.class);
            boolean pass = true;

            if (t == null) {
                t = m.getAnnotation(gov.nasa.jpf.abstraction.FailingTest.class);
                pass = false;
            }

            Annotation c = m.getAnnotation(gov.nasa.jpf.abstraction.Config.class);

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

                if (c == null) {
                    targetConfig.add(new String[0]);
                } else {
                    targetConfig.add(((gov.nasa.jpf.abstraction.Config) c).items());
                }
            }
        }

        if (targetEntries.isEmpty()) {
            targetEntries.add("main([Ljava/lang/String;)V");
            targetShouldPass.add(true);

            Method main = null;

            try {
                main = cls.getMethod("main", String[].class);
            } catch (NoSuchMethodException e) {
            }

            Annotation c = null;

            if (main != null) {
                c = main.getAnnotation(gov.nasa.jpf.abstraction.Config.class);
            }

            if (c == null) {
                targetConfig.add(new String[0]);
            } else {
                targetConfig.add(((gov.nasa.jpf.abstraction.Config) c).items());
            }
        }

        boolean allPassed = true;

        Iterator<String> entryIter = targetEntries.iterator();
        Iterator<Boolean> passIter = targetShouldPass.iterator();
        Iterator<String[]> configIter = targetConfig.iterator();

        while (entryIter.hasNext()) {
            String entry = entryIter.next();
            Boolean expectedPass = passIter.next();
            String[] specificConfig = configIter.next();

            gov.nasa.jpf.Config config = JPF.createConfig(getConfig(specificConfig));

            config.setTargetEntry(entry);

            JPF jpf = new JPF(config);

            try {
                jpf.run();

                allPassed = reducePassed(allPassed, expectedPass, checkPassed(jpf));
            } catch (Throwable t) {
                t.printStackTrace();

                allPassed = reducePassed(allPassed, expectedPass, false);
            }
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
