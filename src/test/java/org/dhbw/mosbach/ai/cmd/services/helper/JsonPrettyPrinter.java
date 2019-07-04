package org.dhbw.mosbach.ai.cmd.services.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonPrettyPrinter {

    private ObjectMapper mapper = new ObjectMapper();

    public String prettyPrint(String json) {
        try {
            Object jsonObject = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }
}
