package org.dhbw.mosbach.ai.cmd.extension.listener.result;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class RunTime {

    public static final RunTime ZERO = new RunTime();

    private AtomicLong startTime = new AtomicLong(0);
    private AtomicLong endTime = new AtomicLong(0);

    public long getStartTime() {
        return startTime.get();
    }

    public void setStartTime(long startTime) {
        this.startTime.set(startTime);
    }

    public long getEndTime() {
        return endTime.get();
    }

    public void setEndTime(long endTime) {
        this.endTime.set(endTime);
    }

    public long getRunTime() {
        return endTime.addAndGet(-startTime.get());
    }

    public boolean isZero() {
        return this.equals(ZERO);
    }

    @Override
    public String toString() {
        return String.format("%.3f", getRunTime() / 1000.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RunTime runTime = (RunTime) o;
        return Objects.equals(startTime, runTime.startTime) &&
                Objects.equals(endTime, runTime.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
}
