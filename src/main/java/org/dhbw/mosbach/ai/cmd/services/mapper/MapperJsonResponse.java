package org.dhbw.mosbach.ai.cmd.services.mapper;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

/**
 * @author 6694964
 * @version 1.0
 */
public abstract class MapperJsonResponse {

    public static final String HTTP_STATUS = "status";

    public static final String STATUS_CODE = "code";

    public static final String STATUS_DESCRIPTION = "description";

    public static final String MESSAGE = "message";

    public static final String REQUESTED_RESOURCE = "requestedResource";

    public static final String ALLOWED_METHODS = "allowedMethods";

    public static final String UNSUPPORTED_CONTENT_TYPE = "unsupportedContentType";

    public static final String EXPECTED_CONTENT_TYPE = "expectedContentType";

    public static final String UNAVAILABLE_REPRESENTATION = "unavailableRepresentation";

    public static final String CAUSE = "cause";

    public static final String SOURCE_LOCATION = "sourceLocation";

    public static JsonObjectBuilder createResponseBuilder(Response.Status status, String message) {
        return Json.createObjectBuilder()
                   .add(HTTP_STATUS, Json.createObjectBuilder()
                                         .add(STATUS_CODE, status.getStatusCode())
                                         .add(STATUS_DESCRIPTION, status.getReasonPhrase()))
                   .add(MESSAGE, message);
    }
}
