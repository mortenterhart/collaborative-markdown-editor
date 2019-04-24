package org.dhbw.mosbach.ai.cmd.websocket;

import org.dhbw.mosbach.ai.cmd.crdt.ActiveDocument;
import org.dhbw.mosbach.ai.cmd.crdt.Message;
import org.dhbw.mosbach.ai.cmd.crdt.MessageBroker;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;

import com.google.gson.Gson;

import javax.inject.Inject;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Endpoint for the web socket communication between the client and the server
 *
 * @author 3040018
 */
@ServerEndpoint(value = "/ws/{docId}", configurator = Configurator.class)
public class Endpoint {
	
	/*
	 * TODO: 
	 * 
	 * 		- Notify clients on connecting and disconnecting users via MessageType.UserJoined / UserLeft
	 * 			- Maybe use onMessage directly after onOpen from JS?
	 * 		- Figure out how to supply length from deletion method of ActiveDocument from client at MessageBroker.transform
	 */
    
	/**
	 * Signed in users
	 */
    private static Set<Session> users = Collections.synchronizedSet(new HashSet<>());
    /**
     * Active docs being worked on by n users
     */
    private static Map<Integer, ActiveDocument> docs = Collections.synchronizedMap(new HashMap<>());
    
    @Inject
    private MessageBroker messageBroker;
    
    @Inject
    private DocDao docDao;
    
    @Inject
    private UserDao userDao;
    
    /**
     * Gets triggered once a web socket connection is opened.
     *
     * @param docId  Given doc id to access
     * @param config  Given config from Configurator.java
     * @param session Current user session
     */
    @OnOpen
    public void onOpen(@PathParam("docId") int docId, EndpointConfig config, Session session) {

        session.getUserProperties().put(CmdConfig.SESSION_USERNAME, config.getUserProperties().get(CmdConfig.SESSION_USERNAME));
        users.add(session);

        if(docs.get(docId) == null)
        	docs.put(docId, new ActiveDocument(new DocDao().getDoc(docId), 0, new ArrayList<>()));

        docs.get(docId).getUsers().add(session);
    }

    /**
     * Gets triggered once a message is sent from the client.<br>
     *
     * @param message Given message
     * @param session Current user session
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    	Message msg = new Gson().fromJson(message, Message.class);
    	ActiveDocument currentDoc = docs.get(msg.getDocId());
    	
    	messageBroker.transform(msg, currentDoc);
    	messageBroker.publish(msg, currentDoc);
    }

    /**
     * Gets triggered once a user closes the connection by logging off or closing the browser tab etc.
     * If it is the last user in the doc, the current state of the doc gets saved into the database.
     *
     * @param session Current user session
     */
    @OnClose
    public void onClose(Session session) {
    	
    	users.remove(session);
        
    	for(int docId : docs.keySet()) {
    		
    		List<Session> workingUsers = docs.get(docId).getUsers();
    		
    		for(Session singleUserSession : workingUsers)
    			if(singleUserSession.equals(session))
    				workingUsers.remove(singleUserSession);
    		
    		if(workingUsers.isEmpty()) {
    			docDao.updateDoc(docs.get(docId).getDoc(), userDao.getUserByName(session.getUserProperties().get(CmdConfig.SESSION_USERNAME).toString()));
    			docs.remove(docId);
    		}
    	}       
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
}
