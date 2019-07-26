package org.dhbw.mosbach.ai.cmd.test.include;

public enum PackageIncludes {

    AUTHENTICATION_SERVICE(
            "org/dhbw/mosbach/ai/cmd/db",
            "org/dhbw/mosbach/ai/cmd/model",
            "org/dhbw/mosbach/ai/cmd/security",
            "org/dhbw/mosbach/ai/cmd/services",
            "org/dhbw/mosbach/ai/cmd/session",
            "org/dhbw/mosbach/ai/cmd/util",
            "org/dhbw/mosbach/ai/cmd/test/config"
    ),

    DOCUMENT_SERVICE(
            "org/dhbw/mosbach/ai/cmd/db",
            "org/dhbw/mosbach/ai/cmd/model",
            "org/dhbw/mosbach/ai/cmd/security",
            "org/dhbw/mosbach/ai/cmd/services",
            "org/dhbw/mosbach/ai/cmd/session",
            "org/dhbw/mosbach/ai/cmd/util",
            "org/dhbw/mosbach/ai/cmd/test/config"
    ),

    COLLABORATOR_SERVICE(
            "org/dhbw/mosbach/ai/cmd/db",
            "org/dhbw/mosbach/ai/cmd/model",
            "org/dhbw/mosbach/ai/cmd/security",
            "org/dhbw/mosbach/ai/cmd/services",
            "org/dhbw/mosbach/ai/cmd/session",
            "org/dhbw/mosbach/ai/cmd/util",
            "org/dhbw/mosbach/ai/cmd/test/config"
    ),

    USER_DAO(
            "org/dhbw/mosbach/ai/cmd/db",
            "org/dhbw/mosbach/ai/cmd/model",
            "org/dhbw/mosbach/ai/cmd/security",
            "org/dhbw/mosbach/ai/cmd/services/serialize",
            "org/dhbw/mosbach/ai/cmd/util",
            "org/dhbw/mosbach/ai/cmd/test/config",
            "org/dhbw/mosbach/ai/cmd/test/helper"
    ),

    DOC_DAO(
            "org/dhbw/mosbach/ai/cmd/db",
            "org/dhbw/mosbach/ai/cmd/model",
            "org/dhbw/mosbach/ai/cmd/security",
            "org/dhbw/mosbach/ai/cmd/services/serialize",
            "org/dhbw/mosbach/ai/cmd/util",
            "org/dhbw/mosbach/ai/cmd/test/config"
    );

    private String[] packages;

    private PackageIncludes(String... packages) {
        this.packages = packages;
    }

    public String[] getPackages() {
        return packages;
    }
}
