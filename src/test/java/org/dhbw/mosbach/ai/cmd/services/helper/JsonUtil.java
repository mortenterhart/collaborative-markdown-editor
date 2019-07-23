package org.dhbw.mosbach.ai.cmd.services.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhbw.mosbach.ai.cmd.services.payload.Payload;
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
        }

        return null;
    }

    public <T extends Payload> String serialize(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException jpe) {
            log.error(jpe.getLocalizedMessage(), jpe);
        }

        return null;
    }

    public <T extends ResponseObject> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException exc) {
            log.error("Could not deserialize json: " + exc.getLocalizedMessage(), exc);
        }

        return null;
    }
}
