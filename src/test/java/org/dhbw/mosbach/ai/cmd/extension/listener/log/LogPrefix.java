package org.dhbw.mosbach.ai.cmd.extension.listener.log;

public enum LogPrefix {
    INFO("INFO"),
    ERROR("ERROR");

    private String prefix;

    private LogPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "[" + prefix + "] ";
    }
}
