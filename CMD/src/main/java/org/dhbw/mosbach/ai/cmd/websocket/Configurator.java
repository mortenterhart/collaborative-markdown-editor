package org.dhbw.mosbach.ai.cmd.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

/**
 * Configuration for the web socket endpoint
 * @author 3040018
 */
public class Configurator extends ServerEndpointConfig.Configurator{

	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		sec.getUserProperties().put(CmdConfig.SESSION_USERNAME, (String)((HttpSession) request.getHttpSession()).getAttribute(CmdConfig.SESSION_USERNAME));
	}
}