package org.dhbw.mosbach.ai.cmd.extension;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunListener.ThreadSafe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@ThreadSafe
public class MethodPrintListener extends RunListener {

    private static final String INFO_PREFIX = "[INFO] ";
    private static final String ERROR_PREFIX = "[ERROR] ";

    private String testClassName;
    private int testCount = 0;
    private int testNumber = 1;

    /**
     * Called before any tests have been run. This may be called on an
     * arbitrary thread.
     *
     * @param description describes the tests to be run
     */
    @Override
    public void testRunStarted(Description description) throws Exception {
        Description testSuite = Description.EMPTY;
        if (!description.getChildren().isEmpty()) {
            testSuite = description.getChildren().get(0);
        }

        testClassName = testSuite.getClassName();

        for (Method method : testSuite.getTestClass().getMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                testCount++;
            }
        }

        printInfo("Starting %d tests from %s", testCount, testSuite.getClassName());

    }

    /**
     * Called when all tests have finished. This may be called on an
     * arbitrary thread.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    @Override
    public void testRunFinished(Result result) throws Exception {
        if (result.wasSuccessful()) {
            printInfo("Finished Tests successfully in %d ms -- Run: %d, Failures: %d, Ignored: %d -- in %s", result.getRunTime(), result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), testClassName);
        } else {
            printError("Finished Tests with errors in %d ms -- Run: %d, Failures: %d, Ignored: %d -- in %s", result.getRunTime(), result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), testClassName);
            result.getFailures().forEach(failure -> printError("  -- %s() in %s: %s", failure.getDescription().getMethodName(), failure.getDescription().getClassName(), failure.getException().toString()));
        }
    }

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    @Override
    public void testStarted(Description description) throws Exception {
        printInfo("(%d) Test started:  %s() in %s", testNumber, description.getMethodName(), description.getClassName());

        for (Annotation testAnnotation : description.getAnnotations()) {
            if (testAnnotation instanceof UsingDataSet) {
                String[] datasets = ((UsingDataSet) testAnnotation).value();
                String datasetList = String.join(", ", datasets);

                if (datasets.length > 1) {
                    printInfo("Using Datasets %s", datasetList);
                } else {
                    printInfo("Using Dataset %s", datasetList);
                }
            } else if (testAnnotation instanceof RunAsClient) {
                printInfo("@RunAsClient: Running test %s() in client mode", description.getMethodName());
            }
        }
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    @Override
    public void testFinished(Description description) throws Exception {
        printInfo("(%d) Finished Test: %s() in %s", testNumber, description.getMethodName(), description.getClassName());
        testNumber++;
    }

    /**
     * Called when an atomic test fails, or when a listener throws an exception.
     *
     * <p>In the case of a failure of an atomic test, this method will be called
     * with the same {@code Description} passed to
     * {@link #testStarted(Description)}, from the same thread that called
     * {@link #testStarted(Description)}.
     *
     * <p>In the case of a listener throwing an exception, this will be called with
     * a {@code Description} of {@link Description#TEST_MECHANISM}, and may be called
     * on an arbitrary thread.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    @Override
    public void testFailure(Failure failure) throws Exception {
        Description testDescription = failure.getDescription();
        printError("(%d) Test failed: %s", testNumber, failure.getException().toString());
        printError("  in: %s() of class %s", testDescription.getMethodName(), testDescription.getClassName());

        printStackTrace(failure);
    }

    /**
     * Called when an atomic test flags that it assumes a condition that is
     * false
     *
     * @param failure describes the test that failed and the
     *                {@link org.junit.AssumptionViolatedException} that was thrown
     */
    @Override
    public void testAssumptionFailure(Failure failure) {
        printError("(%d) Test assumption failed: %s", testNumber, failure.getException().toString());
        printError("  in: %s() of class %s", failure.getDescription().getMethodName(), failure.getDescription().getClassName());

        printStackTrace(failure);
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    @Override
    public void testIgnored(Description description) throws Exception {
        printInfo("(%d) Test ignored: %s() in %s, skipping ...", testNumber, description.getMethodName(), description.getClassName());
        testNumber++;
    }

    private void printInfo(String message) {
        System.out.println(INFO_PREFIX + message);
    }

    private void printInfo(String format, Object... args) {
        printInfo(String.format(format, args));
    }

    private void printError(String message) {
        System.err.println(ERROR_PREFIX + message);
    }

    private void printError(String format, Object... args) {
        printError(String.format(format, args));
    }

    private void printStackTrace(Failure failure) {
        String trace = failure.getTrace();
        printError(trace.replaceAll("\n", "\n" + ERROR_PREFIX));
    }
}
