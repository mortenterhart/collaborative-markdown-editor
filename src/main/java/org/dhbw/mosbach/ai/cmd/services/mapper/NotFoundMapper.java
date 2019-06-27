package org.dhbw.mosbach.ai.cmd.services.mapper;

import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

    @Context
    private UriInfo uriInfo;

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
    public Response toResponse(NotFoundException exception) {
        JsonObject jsonResponse = MapperJsonResponse.createResponseBuilder(Response.Status.NOT_FOUND, exception.getMessage())
                                                    .add(MapperJsonResponse.REQUESTED_RESOURCE, uriInfo.getAbsolutePath().toString())
                                                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(jsonResponse.toString())
                       .build();
    }
}
