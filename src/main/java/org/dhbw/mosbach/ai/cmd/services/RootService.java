package org.dhbw.mosbach.ai.cmd.services;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RequestScoped
@ApplicationPath(ServiceEndpoints.PATH_API_ROOT)
public class RootService extends Application implements RestService {
}
