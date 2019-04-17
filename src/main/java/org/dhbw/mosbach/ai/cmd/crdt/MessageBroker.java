package org.dhbw.mosbach.ai.cmd.crdt;

import java.io.IOException;
import javax.websocket.Session;

import com.google.gson.Gson;

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
			default: break;
		}
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
				session.getBasicRemote().sendText(new Gson().toJson(msg));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}