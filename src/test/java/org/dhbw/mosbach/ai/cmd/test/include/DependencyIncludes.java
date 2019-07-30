package org.dhbw.mosbach.ai.cmd.test.include;

public enum DependencyIncludes {
    USER_DAO("org.mindrot:jbcrypt:0.4");

    private String[] dependencies;

    private DependencyIncludes(String... dependencies) {
        this.dependencies = dependencies;
    }

    public String[] getDependencies() {
        return dependencies;
    }
}
