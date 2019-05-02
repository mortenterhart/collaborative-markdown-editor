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
			case Delete: 	activeDocument.del(msg.getCursorPos(), 1); break;
			case Replace: 	activeDocument.replace(msg.getCursorPos(), 1, msg.getMsg()); break;
			default: throw new RuntimeException("Provided unsupported message type for transform.");
		}
	}
	
	/**
	 * Creates a message if a user joined or left a document, so the other users can be notified.
	 * @param docId Given document id
	 * @param username Given user name to either join or leave
	 * @param messageType Given message type
	 * @return A message object
	 */
	public Message createSystemMessage(int docId, String username, MessageType messageType) {
		
		Message message = new Message();

		message.setDocId(docId);
		message.setMessageType(messageType);
		message.setMsg(username);
		
		return message;
	}

	/**
	 * Publishes the changes to the current document to the other clients
	 * @param msg Given message
	 * @param activeDocument Current active document
	 */
	public void publish(Message msg, ActiveDocument activeDocument) {

		msg.setMsg(activeDocument.getDoc().getContent());
		
		for(Session session : activeDocument.getUsers()) {
			try {
				session.getBasicRemote().sendObject(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}