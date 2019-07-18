package org.dhbw.mosbach.ai.cmd.services.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhbw.mosbach.ai.cmd.services.response.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private ObjectMapper mapper = new ObjectMapper();

    public String prettyPrint(String json) {
        try {
            Object jsonObject = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (IOException exc) {
            log.error("Could not pretty print json: " + exc.getLocalizedMessage(), exc);
            exc.printStackTrace();
            return null;
        }
    }

    public <T extends ResponseObject> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException exc) {
            log.error("Could not deserialize json: " + exc.getLocalizedMessage(), exc);
            exc.printStackTrace();
            return null;
        }
    }
}
