package org.dhbw.mosbach.ai.cmd.extension.listener.util;

import org.dhbw.mosbach.ai.cmd.extension.listener.log.TestLogger;
import org.dhbw.mosbach.ai.cmd.extension.listener.result.RunTime;
import org.dhbw.mosbach.ai.cmd.extension.listener.result.TestSuiteResult;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.SeedDataUsing;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public final class TestCasePrinter {

    private static final TestLogger log = new TestLogger();

    private static final String STRING_LIST_DELIMITER = ", ";

    private final DescriptionScanner scanner = new DescriptionScanner();

    public void printTestSuiteStarted(Description description) {
        Class<?> testClass = scanner.extractFirstTestClass(description);

        int testCount = countTests(testClass);

        String testString = (testCount == 1) ? "test" : "tests";

        Class<? extends Runner> junitRunner = getJUnitRunner(testClass);

        if (junitRunner != null) {
            log.info("Starting %d %s from %s with %s", testCount, testString, testClass.getName(), junitRunner.getSimpleName());
        } else {
            log.info("Starting %d %s from %s", testCount, testString, testClass.getName());
        }
    }

    private int countTests(Class<?> testClass) {
        int count = 0;
        for (Method method : testClass.getMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                count++;
            }
        }

        return count;
    }

    private Class<? extends Runner> getJUnitRunner(Class<?> testClass) {
        RunWith runAnnotation = testClass.getAnnotation(RunWith.class);
        if (runAnnotation != null) {
            return runAnnotation.value();
        }

        return null;
    }

    public void printTestAnnotations(Collection<Annotation> testAnnotations) {
        for (Annotation annotation : testAnnotations) {
            if (annotation instanceof DataSource) {
                log.info("Using Datasource %s", ((DataSource) annotation).value());
            } else if (annotation instanceof SeedDataUsing) {
                log.info("Seeding database with strategy %s", ((SeedDataUsing) annotation).value());
            } else if (annotation instanceof UsingDataSet) {
                String[] datasets = ((UsingDataSet) annotation).value();
                if (datasets.length == 1) {
                    log.info("Using Dataset %s", datasets[0]);
                } else {
                    log.info("Using Datasets %s", String.join(STRING_LIST_DELIMITER, datasets));
                }
            } else if (annotation instanceof ShouldMatchDataSet) {
                String[] datasets = ((ShouldMatchDataSet) annotation).value();
                if (datasets.length == 1) {
                    log.info("Database should match dataset %s", datasets[0]);
                } else {
                    log.info("Database should match datasets %s", String.join(STRING_LIST_DELIMITER, datasets));
                }
            } else if (annotation instanceof RunAsClient) {
                log.info("@RunAsClient: Running test in client mode");
            } else if (annotation instanceof Transactional) {
                TransactionMode transactionMode = ((Transactional) annotation).value();
                log.info("Transaction mode set to %s", transactionMode.name());
            } else if (annotation instanceof ApplyScriptBefore) {
                String[] scripts = ((ApplyScriptBefore) annotation).value();
                log.info("Applying scripts before tests: %s", String.join(STRING_LIST_DELIMITER, scripts));
            } else if (annotation instanceof ApplyScriptAfter) {
                String[] scripts = ((ApplyScriptAfter) annotation).value();
                log.info("Applying scripts after tests: %s", String.join(STRING_LIST_DELIMITER, scripts));
            } else if (annotation instanceof Cleanup) {
                Cleanup cleanup = (Cleanup) annotation;
                log.info("Cleaning up database in test phase %s with strategy %s", cleanup.phase(), cleanup.strategy());
            } else if (annotation instanceof PersistenceTest) {
                log.info("Running test as persistence test");
            }
        }
    }

    public void printTestAnnotations(Annotation[] annotations) {
        printTestAnnotations(Arrays.asList(annotations));
    }

    public void printTestEndResults(TestSuiteResult result, String testClassName) {
        if (result.wasSuccessful()) {
            log.info("Finished Tests successfully in %s s -- Run: %d, Failures: %d, Ignored: %d -- in %s",
                    result.getRunTime(), result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), testClassName);
        } else {
            log.error("Finished Tests with errors in %s s -- Run: %d, Failures: %d, Ignored: %d -- in %s",
                    result.getRunTime(), result.getRunCount(), result.getFailureCount(), result.getIgnoreCount(), testClassName);

            result.getFailures().forEach(failure -> {
                String methodName = failure.getDescription().getMethodName();
                if (methodName != null) {
                    log.error("  -- %s() in %s: %s", methodName, failure.getDescription().getClassName(), failure.getException().toString());
                } else {
                    log.error("  -- in %s: %s", failure.getDescription().getClassName(), failure.getException().toString());
                }

                Throwable cause = failure.getException().getCause();
                while (cause != null) {
                    log.error("       Caused by: %s", cause.toString());
                    cause = cause.getCause();
                }
            });
        }
    }

    public void printTestStarted(int testNumber, Description description) {
        log.info("(%d) Test started: %s() in %s", testNumber, description.getMethodName(), description.getClassName());
    }

    public void printTestFinished(int testNumber, Description description) {
        printTestFinished(testNumber, description, RunTime.ZERO);
    }

    public void printTestFinished(int testNumber, Description description, RunTime runTime) {
        if (runTime.isZero()) {
            log.info("(%d) Finished Test: %s() in %s", testNumber, description.getMethodName(), description.getClassName());
        } else {
            log.info("(%d) Finished Test in %s s: %s() in %s", testNumber, runTime, description.getMethodName(), description.getClassName());
        }
    }

    public void printTestFailure(int testNumber, Failure failure) {
        Description testDescription = failure.getDescription();
        log.error("(%d) Test failed: %s", testNumber, failure.getException().toString());
        if (testDescription.getMethodName() != null) {
            log.error("  in: %s() of class %s", testDescription.getMethodName(), testDescription.getClassName());
        } else {
            log.error("  in: class %s", testDescription.getClassName());
        }

        log.stackTrace(failure);
    }

    public void printTestAssumptionFailure(int testNumber, Failure failure) {
        log.error("(%d) Test assumption failed: %s", testNumber, failure.getException().toString());
        log.error("  in: %s() of class %s", failure.getDescription().getMethodName(), failure.getDescription().getClassName());

        log.stackTrace(failure);
    }

    public void printTestIgnored(int testNumber, Description description) {
        log.info("(%d) Test ignored: %s() in %s, skipping ...", testNumber, description.getMethodName(), description.getClassName());
    }
}
