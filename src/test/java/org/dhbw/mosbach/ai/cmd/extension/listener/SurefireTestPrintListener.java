package org.dhbw.mosbach.ai.cmd.extension.listener;

import org.dhbw.mosbach.ai.cmd.extension.listener.result.RunTime;
import org.dhbw.mosbach.ai.cmd.extension.listener.result.TestSuiteResult;
import org.dhbw.mosbach.ai.cmd.extension.listener.util.DescriptionScanner;
import org.dhbw.mosbach.ai.cmd.extension.listener.util.TestCasePrinter;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunListener.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class SurefireTestPrintListener extends RunListener {

    private final TestCasePrinter testPrinter = new TestCasePrinter();
    private final DescriptionScanner scanner = new DescriptionScanner();

    private RunTime suiteRunTime = new RunTime();
    private RunTime testRunTime = new RunTime();

    private String testClassName;
    private AtomicInteger testNumber = new AtomicInteger(1);

    /**
     * Called before any tests have been run. This may be called on an
     * arbitrary thread.
     *
     * @param description describes the tests to be run
     */
    @Override
    public void testRunStarted(Description description) throws Exception {
        testPrinter.printTestSuiteStarted(description);

        Class<?> testClass = scanner.extractFirstTestClass(description);
        this.testClassName = testClass.getName();

        testPrinter.printTestAnnotations(testClass.getAnnotations());

        suiteRunTime.setStartTime(System.currentTimeMillis());
    }

    /**
     * Called when all tests have finished. This may be called on an
     * arbitrary thread.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    @Override
    public void testRunFinished(Result result) throws Exception {
        suiteRunTime.setEndTime(System.currentTimeMillis());

        testPrinter.printTestEndResults(new TestSuiteResult(result, suiteRunTime), testClassName);
    }

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    @Override
    public void testStarted(Description description) throws Exception {
        testPrinter.printTestStarted(testNumber.get(), description);
        testPrinter.printTestAnnotations(description.getAnnotations());

        testRunTime.setStartTime(System.currentTimeMillis());
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    @Override
    public void testFinished(Description description) throws Exception {
        testRunTime.setEndTime(System.currentTimeMillis());

        testPrinter.printTestFinished(testNumber.get(), description, testRunTime);
        testNumber.incrementAndGet();
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
        testPrinter.printTestFailure(testNumber.get(), failure);
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
        testPrinter.printTestAssumptionFailure(testNumber.get(), failure);
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    @Override
    public void testIgnored(Description description) throws Exception {
        testPrinter.printTestIgnored(testNumber.get(), description);
        testNumber.incrementAndGet();
    }
}
