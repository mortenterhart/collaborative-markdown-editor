package org.dhbw.mosbach.ai.cmd.services.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseObject;

import java.io.IOException;

public final class JsonUtil {

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

    public <T extends ResponseObject> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }
}
