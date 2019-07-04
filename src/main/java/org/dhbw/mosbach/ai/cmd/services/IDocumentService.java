package org.dhbw.mosbach.ai.cmd.services;

import org.dhbw.mosbach.ai.cmd.services.payload.DocumentAccessModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentInsertionModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentRemovalModel;
import org.dhbw.mosbach.ai.cmd.services.payload.DocumentTransferModel;
import org.dhbw.mosbach.ai.cmd.services.validation.document.DocumentInsertionValidation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public interface IDocumentService {

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response addDocument(@NotNull DocumentInsertionModel model, @Context HttpServletRequest request);

    @DELETE
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response removeDocument(@NotNull DocumentRemovalModel model, @Context HttpServletRequest request);

    @POST
    @Path("/hasAccess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response hasDocumentAccess(@NotNull DocumentAccessModel model, @Context HttpServletRequest request);

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDocuments(@Context HttpServletRequest request);

    @PATCH
    @Path("/transferOwnership")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response transferOwnership(@NotNull DocumentTransferModel model, @Context HttpServletRequest request);
}
