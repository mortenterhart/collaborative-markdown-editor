package org.dhbw.mosbach.ai.cmd.services.helper;

import java.util.Map;
import java.util.TreeMap;

public final class ClientJson {

    private ClientJson() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Map<String, Object> mappings = new TreeMap<>();

        private Builder() {
        }

        public Builder addKey(String key, Object value) {
            mappings.put(key, value);
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("{");
            mappings.forEach((key, value) -> {
                builder.append("\"").append(escapeJson(key)).append("\":");

                if (value == null) {
                    builder.append("null");
                } else if (value instanceof Byte) {
                    builder.append((byte) value);
                } else if (value instanceof Short) {
                    builder.append((short) value);
                } else if (value instanceof Integer) {
                    builder.append((int) value);
                } else if (value instanceof Long) {
                    builder.append((long) value);
                } else if (value instanceof Float) {
                    builder.append((float) value);
                } else if (value instanceof Double) {
                    builder.append((double) value);
                } else if (value instanceof Character) {
                    builder.append((char) value);
                } else if (value instanceof Boolean) {
                    builder.append((boolean) value);
                } else {
                    builder.append("\"").append(escapeJson(value.toString())).append("\"");
                }

                builder.append(",");
            });

            if (!mappings.isEmpty()) {
                builder.replace(builder.length() - 1, builder.length(), "");
            }

            builder.append("}");
            return builder.toString();
        }

        private String escapeJson(String value) {
            return value.replaceAll("([\"\'\\\\])", "\\\\$1");
        }
    }
}
