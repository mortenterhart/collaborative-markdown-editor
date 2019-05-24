package org.dhbw.mosbach.ai.cmd.util;

/**
 * Message types send via web sockets
 * 
 * @author 3040018
 */
public enum MessageType {
	
	Insert("Insert"),
	Delete("Delete"),
	UserJoined("UserJoined"),
	UserLeft("UserLeft"),
	ContentInit("ContentInit"),
	DocumentTitle("DocumentTitle"),
	UsersInit("UsersInit"),
	ChatMessage("ChatMessage"),
	WrongDocId("WrongDocId");
	
	private String messageType;
	
	private MessageType(String messageType) {
		this.messageType = messageType;
	}
	
	@Override
	public String toString() {
		return this.messageType;
	}
}