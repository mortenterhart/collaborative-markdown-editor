package org.dhbw.mosbach.ai.cmd.extension.listener.result;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.Serializable;
import java.util.List;

public class TestSuiteResult implements Serializable {

    private static final long serialVersionUID = -3536821423119548367L;

    private final RunTime runTime;
    private final int runCount;
    private final int failureCount;
    private final int ignoreCount;

    private final List<Failure> failures;

    public TestSuiteResult(Result sourceResult, RunTime runTime) {
        this.runTime = runTime;
        this.runCount = sourceResult.getRunCount();
        this.failureCount = sourceResult.getFailureCount();
        this.ignoreCount = sourceResult.getIgnoreCount();
        this.failures = sourceResult.getFailures();
    }

    TestSuiteResult(ResultAccumulator accumulator, RunTime runTime) {
        this.runTime = runTime;
        this.runCount = accumulator.getRunCount();
        this.failureCount = accumulator.getFailureCount();
        this.ignoreCount = accumulator.getIgnoreCount();
        this.failures = accumulator.getFailures();
    }

    public boolean wasSuccessful() {
        return failureCount == 0;
    }

    public RunTime getRunTime() {
        return runTime;
    }

    public int getRunCount() {
        return runCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getIgnoreCount() {
        return ignoreCount;
    }

    public List<Failure> getFailures() {
        return failures;
    }
}
