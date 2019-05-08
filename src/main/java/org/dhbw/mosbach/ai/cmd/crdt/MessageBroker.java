package org.dhbw.mosbach.ai.cmd.crdt;

import javax.websocket.Session;

import org.dhbw.mosbach.ai.cmd.util.MessageType;

/**
 * Wrapper class to keep a consistent document state and publish updates to all the connected clients
 * 
 * @author 3040018
 */
public class MessageBroker {

	/**
	 * Transforms a given message according to the provided MessageType
	 * @param msg Given message
	 * @param activeDocument Current active document
	 */
	public void transform(Message msg, ActiveDocument activeDocument) {

		switch(msg.getMessageType()) {
			case Insert: 	activeDocument.insert(msg.getCursorPos(), msg.getMsg()); break;
			case Delete: 	activeDocument.del(msg.getCursorPos(), msg.getMsg().length()); break;
			default: throw new RuntimeException("Provided unsupported message type for transform.");
		}
	}
	
	/**
	 * Creates a message if a user joined or left a document, so the other users can be notified.
	 * Also creates a message to send the doc contents if a user just joined
	 * @param docId Given document id
	 * @param msg Given message content depending on the type of system message
	 * @param messageType Given message type
	 * @return A message object
	 */
	public Message createSystemMessage(int docId, String msg, MessageType messageType) {
		
		Message message = new Message();

		message.setCursorPos(-1);
		message.setDocId(docId);
		message.setMessageType(messageType);
		message.setMsg(msg);
		message.setUserId(-1);
		
		return message;
	}

	/**
	 * Publishes the changes to the current document to the other clients
	 * @param msg Given message
	 * @param activeDocument Current active document
	 */
	public void publishToOtherUsers(Message msg, ActiveDocument activeDocument, Session currentUserSession) {
		
		for(Session session : activeDocument.getUsers()) {
			try {
				if (session != currentUserSession)
					session.getBasicRemote().sendObject(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Publishes a message to a single user only. Used to initially 
	 * send the doc contents to a user upon connecting
	 * @param msg Given message
	 * @param session Session of user to send the message to
	 */
	public void publishToSingleUser(Message msg, Session session) {
		try {
			session.getBasicRemote().sendObject(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}