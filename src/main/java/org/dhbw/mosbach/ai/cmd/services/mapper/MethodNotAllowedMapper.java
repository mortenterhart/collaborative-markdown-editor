package org.dhbw.mosbach.ai.cmd.services.mapper;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Set;

/**
 * @author 6694964
 * @version 1.0
 */
@Provider
public class MethodNotAllowedMapper implements ExceptionMapper<NotAllowedException> {

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
    public Response toResponse(NotAllowedException exception) {
        Set<String> allowedMethods = exception.getResponse().getAllowedMethods();
        String allowHeader = allowedMethods.stream().reduce((m1, m2) -> m1 + ", " + m2).orElse(HttpMethod.OPTIONS);

        JsonObject jsonResponse = MapperJsonResponse.createResponseBuilder(Response.Status.METHOD_NOT_ALLOWED, exception.getMessage())
                                                    .add(MapperJsonResponse.ALLOWED_METHODS, createJsonArray(allowedMethods))
                                                    .build();

        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(jsonResponse)
                       .header(HttpHeaders.ALLOW, allowHeader)
                       .build();
    }

    private JsonArray createJsonArray(Set<String> methods) {
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        methods.forEach(m -> jsonArray.add(m));

        return jsonArray.build();
    }
}
