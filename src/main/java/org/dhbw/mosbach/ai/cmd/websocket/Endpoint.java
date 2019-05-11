package org.dhbw.mosbach.ai.cmd.websocket;

import org.dhbw.mosbach.ai.cmd.crdt.ActiveDocument;
import org.dhbw.mosbach.ai.cmd.crdt.Message;
import org.dhbw.mosbach.ai.cmd.crdt.MessageBroker;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.MessageType;

import javax.inject.Inject;
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
import java.util.Map;

/**
 * End point for the web socket communication between the client and the server
 *
 * @author 3040018
 */
@ServerEndpoint(value = "/ws/{docId}/{username}/{userid}", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Endpoint {

    /**
     * Active docs being worked on by n users
     */
    private static Map<Integer, ActiveDocument> docs = Collections.synchronizedMap(new HashMap<>());
    
    @Inject
    private MessageBroker messageBroker;
    
	/**
     * Gets triggered once a web socket connection is opened.
	 * @param docId Given document id
	 * @param username Given user name of current user
	 * @param session Current user session
	 */
    @OnOpen
    public void onOpen(@PathParam("docId") int docId, @PathParam(CmdConfig.SESSION_USERNAME) String username, @PathParam(CmdConfig.SESSION_USERID) int userId, Session session) {

        session.getUserProperties().put(CmdConfig.SESSION_USERNAME, username);
        session.getUserProperties().put(CmdConfig.SESSION_USERID, userId);

        Doc doc = null;
        
        if(docs.get(docId) == null) {
        	doc = new DocDao().getDoc(docId);
        	docs.put(docId, new ActiveDocument(doc, 0, new ArrayList<>()));
        }
        	
        if(doc == null)
        	doc = docs.get(docId).getDoc();
        
        if(doc.getContent() == null)
        	docs.get(docId).getDoc().setContent("");
        
        docs.get(docId).getUsers().add(session);
        
        Message contentInitMsg = messageBroker.createSystemMessage(userId, docId, doc.getContent(), MessageType.ContentInit);
        messageBroker.publishToSingleUser(contentInitMsg, session);
        
        Message documentTitleMsg = messageBroker.createSystemMessage(userId, docId,  doc.getName(), MessageType.DocumentTitle);
        messageBroker.publishToSingleUser(documentTitleMsg, session);
        
        Message userInitMsg = messageBroker.createSystemMessage(userId, docId, messageBroker.getActiveUsers(docs.get(docId).getUsers(), session), MessageType.UsersInit);
        messageBroker.publishToSingleUser(userInitMsg, session);
        
        Message userJoinedMsg = messageBroker.createSystemMessage(userId, docId, username, MessageType.UserJoined);
        messageBroker.publishToOtherUsers(userJoinedMsg, docs.get(docId), session);
    }

    /**
     * Gets triggered once a message is sent from the client.<br>
     *
     * @param msg Given message
     * @param session Current user session
     */
    @OnMessage
    public void onMessage(Message msg, Session session) {
    	
    	System.out.println("In onMessage");
    	System.out.println(msg.toString());

    	ActiveDocument currentDoc = docs.get(msg.getDocId());
    	
    	messageBroker.publishToOtherUsers(msg, currentDoc, session);
    	messageBroker.transform(msg, currentDoc);
  
    	System.out.println("Message after transform:\n\t" + currentDoc.getDoc().getContent());
    }

    /**
     * Gets triggered once a user closes the connection by logging off or closing the browser tab etc.
     * If it is the last user in the doc, the current state of the doc gets saved into the database.
     *
     * @param session Current user session
     */
    @OnClose
    public void onClose(Session session) {

    	for(int docId : docs.keySet()) {
    		for(Session singleUserSession : docs.get(docId).getUsers()) {
    			if(singleUserSession.equals(session)) {
    				
    				docs.get(docId).getUsers().remove(singleUserSession);
    				
    				String userName = (String)singleUserSession.getUserProperties().get(CmdConfig.SESSION_USERNAME);
    				int userId = (int) singleUserSession.getUserProperties().get(CmdConfig.SESSION_USERID);
    				
    		        Message userLeftdMsg = messageBroker.createSystemMessage(userId,  docId, userName, MessageType.UserLeft);
    		        messageBroker.publishToOtherUsers(userLeftdMsg, docs.get(docId), session);
    		        
    		        break;
    			}	
    		}	
    		
    		if(docs.get(docId).getUsers().isEmpty()) {
    			
    			Doc activeDoc = docs.get(docId).getDoc();
    			activeDoc.setUuser(new UserDao().getUserByName(session.getUserProperties().get(CmdConfig.SESSION_USERNAME).toString()));
    			
    			DocDao docDao = new DocDao();
    			docDao.updateDoc(activeDoc);
    			
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
