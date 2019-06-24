package org.dhbw.mosbach.ai.cmd.services;

/**
 * This abstract class defines constants for the URL paths referencing the available
 * API endpoints. The paths can be hierarchically ordered so that they appear behind
 * each other in the URL path.
 *
 * @author 6694964
 * @version 1.3
 * @see RootService
 * @see AuthenticationService
 * @see CollaboratorService
 * @see DocumentService
 */
public abstract class ServiceEndpoints {

    /**
     * The path prefix for all other endpoints pretended by the {@link RootService}
     */
    public static final String PATH_API_ROOT = "/api";

    /**
     * The path prefix for services in the {@link AuthenticationService}
     */
    public static final String PATH_AUTHENTICATION = "/authentication";

    /**
     * The path prefix for services in the {@link DocumentService}
     */
    public static final String PATH_DOCUMENT = "/document";

    /**
     * The path prefix for services in the {@link CollaboratorService}
     */
    public static final String PATH_COLLABORATOR = "/collaborators";
}
