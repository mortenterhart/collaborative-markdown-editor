package org.dhbw.mosbach.ai.cmd.websocket;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Endpoint for the web socket communication between the client and the server
 *
 * @author 3040018
 */
@ServerEndpoint(value = "/ws", configurator = Configurator.class)
public class Endpoint {

    /**
     * Currently online users
     */
    private static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

    /**
     * Gets triggered once a web socket connection is opened.
     *
     * @param config  Given config from Configurator.java
     * @param session Current user session
     */
    @OnOpen
    public void onOpen(EndpointConfig config, Session session) {
        session.getUserProperties().put(CmdConfig.SESSION_USERNAME, config.getUserProperties().get(CmdConfig.SESSION_USERNAME));
        users.add(session);
    }

    /**
     * Gets triggered once sendMessage is called from the client.<br>
     *
     * @param message Given message
     * @param session Current user session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        spreadMsg(message, (String) session.getUserProperties().get(CmdConfig.SESSION_USERNAME));
    }

    /**
     * Gets triggered once a user closes the connection by logging off or closing the browser tab etc.
     *
     * @param session Current user session
     */
    @OnClose
    public void onClose(Session session) {
        users.remove(session);
        // TODO: Persist document in db
    }

    /**
     * Gets triggered on an error.
     *
     * @param T Given Exception
     */
    @OnError
    public void onError(Throwable T) {
        T.printStackTrace();
    }

    private void spreadMsg(String msg, String user) {

        for (Session s : users) {
            try {
                s.getBasicRemote().sendText(msg);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
