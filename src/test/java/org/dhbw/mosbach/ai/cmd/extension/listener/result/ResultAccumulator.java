package org.dhbw.mosbach.ai.cmd.extension.listener.result;

import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultAccumulator {

    private AtomicInteger runCount = new AtomicInteger(0);
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger ignoreCount = new AtomicInteger(0);

    private List<Failure> failures = new ArrayList<>();

    public void incrementRunCount() {
        this.runCount.incrementAndGet();
    }

    public int getRunCount() {
        return runCount.get();
    }

    public void incrementFailureCount() {
        this.failureCount.incrementAndGet();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public void incrementIgnoreCount() {
        this.ignoreCount.incrementAndGet();
    }

    public int getIgnoreCount() {
        return ignoreCount.get();
    }

    public void addFailure(Failure failure) {
        this.failures.add(failure);
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public TestSuiteResult buildResult(RunTime runTime) {
        return new TestSuiteResult(this, runTime);
    }

    public void reset() {
        this.runCount.set(0);
        this.failureCount.set(0);
        this.ignoreCount.set(0);
        this.failures.clear();
    }
}
