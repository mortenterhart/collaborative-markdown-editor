package org.dhbw.mosbach.ai.cmd.services;

/**
 * The {@code RestEndpoint} is an abstraction of a REST compliant endpoint
 * and facilitates user interactions with the backend of the application.
 *
 * Implementations of this interface can provide several services which are
 * accessed by a distinct resource locator commonly referred to as URL. Each
 * service defines a HTTP method that needs to be applied in order to behave
 * as the method assumes. For example, a service removing a document could be
 * accessed by the {@code DELETE} method of the HTTP protocol.
 *
 * REST endpoints gain access to the user session by injecting the user's HTTP
 * request in form of {@link javax.servlet.http.HttpServletRequest} as a
 * contextual injection. The {@link org.dhbw.mosbach.ai.cmd.session.SessionUtil}
 * directly provides such injection and besides offers access to the session
 * information such as the user object or operations such as invalidating the
 * current session.
 *
 * An implementation of a REST endpoint may provide services which accept
 * request models that contain the actual payload of a user request and return
 * a response with appropriate information about the processing status of the
 * request. In any case, request and response models should not be {@code null}.
 *
 * @author 6694964
 * @version 1.3
 *
 * @see AuthenticationService
 * @see CollaboratorService
 * @see DocumentService
 * @see org.dhbw.mosbach.ai.cmd.session.SessionUtil
 */
public interface RestEndpoint {
}
