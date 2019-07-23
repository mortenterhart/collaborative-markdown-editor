package org.dhbw.mosbach.ai.cmd.services.helper;

import org.dhbw.mosbach.ai.cmd.services.ServiceEndpoints;
import org.dhbw.mosbach.ai.cmd.services.payload.TestLoginModel;
import org.dhbw.mosbach.ai.cmd.testconfig.TestConfig;
import org.dhbw.mosbach.ai.cmd.testconfig.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public final class Authenticator {

    private static final Logger log = LoggerFactory.getLogger(Authenticator.class);

    private static final String API_PREFIX = ServiceEndpoints.PATH_API_ROOT.substring(1);

    private Authenticator() {
    }

    public static Response authenticate(URI deploymentBaseURI, TestUser testUser) {
        final WebTarget target = ClientBuilder.newClient().target(deploymentBaseURI.resolve(API_PREFIX + TestConfig.AUTHENTICATION_LOGIN_PATH));

        log.info("POST {}", target.getUri().getPath());

        return target.request(MediaType.APPLICATION_JSON)
                     .post(Entity.json(new TestLoginModel(testUser)));
    }
}
