package org.dhbw.mosbach.ai.cmd.services;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * The {@code RootService} is the main initialization point for the underlying
 * REST compliant endpoints by extending the {@link Application} in order to
 * create an operational JAX-RS application. It defines the components and supplies
 * additional metadata to the underlying services such as the application root
 * path used to create the basic path prefix for all underlying services.
 *
 * The underlying REST endpoints may extend this root service and create own
 * services under the root path prefix to offer certain functionality. The root
 * service makes no assumptions about the purpose of functionality that can be
 * provided by a service.
 *
 * @author 6694964
 * @version 1.3
 * @see RestEndpoint
 * @see AuthenticationService
 * @see CollaboratorService
 * @see DocumentService
 */
@ApplicationScoped
@ApplicationPath(ServiceEndpoints.PATH_API_ROOT)
public class RootService extends Application implements RestEndpoint {
}
