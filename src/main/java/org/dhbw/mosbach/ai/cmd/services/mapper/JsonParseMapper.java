package org.dhbw.mosbach.ai.cmd.services.mapper;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author 6694964
 * @version 1.1
 */
@Provider
public class JsonParseMapper implements ExceptionMapper<JsonParseException> {

    /**
     * Map an exception to a {@link Response}. Returning
     * {@code null} results in a {@link Response.Status#NO_CONTENT}
     * response. Throwing a runtime exception results in a
     * {@link Response.Status#INTERNAL_SERVER_ERROR} response.
     *
     * @param exception the exception to map to a response.
     * @return a response mapped from the supplied exception.
     */
    @Override
    public Response toResponse(JsonParseException exception) {
        JsonLocation location = exception.getLocation();

        JsonObject jsonResponse = MapperJsonResponse.createResponseBuilder(Response.Status.BAD_REQUEST, exception.getMessage())
                                                    .add(MapperJsonResponse.SOURCE_LOCATION, Json.createObjectBuilder()
                                                                                                 .add("line", location.getLineNr())
                                                                                                 .add("column", location.getColumnNr())
                                                                                                 .add("byteOffset", location.getByteOffset()))
                                                    .build();

        return Response.status(Response.Status.BAD_REQUEST)
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(jsonResponse.toString())
                       .build();
    }
}
