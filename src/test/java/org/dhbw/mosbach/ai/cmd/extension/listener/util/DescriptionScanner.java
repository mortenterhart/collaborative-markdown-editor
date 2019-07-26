package org.dhbw.mosbach.ai.cmd.extension.listener.util;

import org.junit.runner.Description;

public final class DescriptionScanner {

    public Class<?> extractFirstTestClass(Description description) {
        if (description == null) {
            return void.class;
        }

        if (description.getTestClass() != null) {
            return description.getTestClass();
        }

        for (Description child : description.getChildren()) {
            if (child != null && child.getTestClass() != null) {
                return child.getTestClass();
            }
        }

        return void.class;
    }
}
