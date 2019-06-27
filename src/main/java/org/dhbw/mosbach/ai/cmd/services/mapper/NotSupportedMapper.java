package org.dhbw.mosbach.ai.cmd.services.mapper;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotSupportedMapper implements ExceptionMapper<NotSupportedException> {

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
    public Response toResponse(NotSupportedException exception) {
        String unsupportedMediaType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (unsupportedMediaType == null) {
            unsupportedMediaType = "<no type>";
        }

        JsonObject jsonResponse = MapperJsonResponse.createResponseBuilder(Response.Status.UNSUPPORTED_MEDIA_TYPE, exception.getMessage())
                                                    .add(MapperJsonResponse.UNSUPPORTED_CONTENT_TYPE, unsupportedMediaType)
                                                    .add(MapperJsonResponse.EXPECTED_CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                                    .build();

        return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(jsonResponse.toString())
                       .build();
    }
}
