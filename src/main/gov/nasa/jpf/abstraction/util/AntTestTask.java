package gov.nasa.jpf.abstraction.util;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTaskMirror;

import java.util.List;
import java.util.LinkedList;

public class AntTestTask extends JUnitTask {

    private int total = 0;
    private List<String> errorTests = new LinkedList<String>();
    private List<String> failureTests = new LinkedList<String>();
    private List<String> timedOutTests = new LinkedList<String>();
    private List<String> crashedTests = new LinkedList<String>();

    public AntTestTask() throws Exception {
    }

    @Override
    protected void actOnTestResult(JUnitTask.TestResultHolder result, JUnitTest test, String name) {
        ++total;

        switch (result.exitCode) {
            case JUnitTaskMirror.JUnitTestRunnerMirror.SUCCESS:
                break;
            case JUnitTaskMirror.JUnitTestRunnerMirror.FAILURES:
                failureTests.add(test.getName());
                break;
            case JUnitTaskMirror.JUnitTestRunnerMirror.ERRORS:
                errorTests.add(test.getName());
                break;
        }

        if (result.timedOut) {
            timedOutTests.add(test.getName());
        }

        if (result.crashed) {
            crashedTests.add(test.getName());
        }

        super.actOnTestResult(result, test, name);

        System.out.println();
        System.out.println();
        System.out.println();
    }

    // UGLY to do this in cleanup but it is the only straightforward way to do so in current JUnitTask implementation
    @Override
    protected void cleanup() {
        super.cleanup();

        System.out.println("Total: " + total + "; Failed: " + (failureTests.size() + errorTests.size() + crashedTests.size()) + "; Timed out: " + timedOutTests.size());

        if (!failureTests.isEmpty() || !errorTests.isEmpty() || !crashedTests.isEmpty()) {
            System.out.println("Failed tests:");

            for (String testName : failureTests) {
                System.out.println("\t" + testName);
            }

            for (String testName : errorTests) {
                System.out.println("\t" + testName);
            }

            for (String testName : crashedTests) {
                System.out.println("\t" + testName);
            }
        }

        if (!timedOutTests.isEmpty()) {
            System.out.println("Timed out tests:");

            for (String testName : timedOutTests) {
                System.out.println("\t" + testName);
            }
        }
    }
}
