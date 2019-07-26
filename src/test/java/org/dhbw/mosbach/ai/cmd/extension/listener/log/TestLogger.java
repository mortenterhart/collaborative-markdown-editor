package org.dhbw.mosbach.ai.cmd.extension.listener.log;

import org.junit.runner.notification.Failure;

public class TestLogger {

    public void info(String message) {
        System.out.println(LogPrefix.INFO + message);
    }

    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    public void error(String message) {
        System.err.println(LogPrefix.ERROR + message);
    }

    public void error(String format, Object... args) {
        error(String.format(format, args));
    }

    public void stackTrace(Failure failure) {
        String trace = failure.getTrace();
        error(trace.replaceAll("\n", "\n" + LogPrefix.ERROR));
    }
}
