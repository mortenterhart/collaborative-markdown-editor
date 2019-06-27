package org.dhbw.mosbach.ai.cmd.services.mapper;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAcceptableMapper implements ExceptionMapper<NotAcceptableException> {

    @Context
    private HttpServletRequest request;

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
    public Response toResponse(NotAcceptableException exception) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        JsonObject jsonResponse = MapperJsonResponse.createResponseBuilder(Response.Status.NOT_ACCEPTABLE, exception.getMessage())
                                                    .add(MapperJsonResponse.CAUSE, "The accepted representation types are not available at the server: " + acceptHeader)
                                                    .add(MapperJsonResponse.UNAVAILABLE_REPRESENTATION, acceptHeader)
                                                    .build();

        return Response.notAcceptable(Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE).build())
                       .entity(jsonResponse.toString()).type(MediaType.APPLICATION_JSON_TYPE)
                       .build();
    }
}
