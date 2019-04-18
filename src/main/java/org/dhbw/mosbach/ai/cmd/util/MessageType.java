package org.dhbw.mosbach.ai.cmd.util;

public enum MessageType {

	Insert("Insert"),
	Delete("Delete"),
	Replace("Replace"),
	UserJoined("UserJoined"),
	UserLeft("UserLeft");
	
	private String messageType;
	
	private MessageType(String messageType) {
		this.messageType = messageType;
	}
	
	@Override
	public String toString() {
		return this.messageType;
	}
}