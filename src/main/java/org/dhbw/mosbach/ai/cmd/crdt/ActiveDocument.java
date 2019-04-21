package org.dhbw.mosbach.ai.cmd.crdt;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import org.dhbw.mosbach.ai.cmd.model.Doc;

/**
 * Representation of an active document which is being worked on by n users at the same time
 * 
 * @author 3040018
 */
public class ActiveDocument {
	
	private Doc doc;
	private long state;
	private List<Session> users;
	
	public ActiveDocument() {
		
		this.state = 0;
		this.users = new ArrayList<>();
	}
	public ActiveDocument(Doc doc, long state, List<Session> users) {
		super();
		this.doc = doc;
		this.state = state;
		this.users = users;
	}
	public Doc getDoc() {
		return doc;
	}
	public void setDoc(Doc doc) {
		this.doc = doc;
	}
	public long getState() {
		return state;
	}
	public void setState(long state) {
		this.state = state;
	}
	public List<Session> getUsers() {
		return users;
	}
	public void setUsers(List<Session> users) {
		this.users = users;
	}	
	
	/**
	 * Inserts a message from a user at a given index
	 * @param i Given index
	 * @param msg Given message
	 */
	public void insert(int i, String msg){

		doc.setContent(new StringBuilder()
					 	.append(doc.getContent().substring(0, i - 1))
					 	.append(msg)
					 	.append(doc.getContent().substring(i - 1))
					 	.toString());
		state++;
	}

	/**
	 * Deletes a message from a user of a given length at a given index
	 * @param i Given index
	 * @param length Given length of how much to delete after the index
	 */
	public void del(int i, int length){

		doc.setContent(new StringBuilder()
					 	.append(doc.getContent().substring(0, i - 1))
					 	.append(doc.getContent().substring(i - 1 + length))
					 	.toString());
		state++;
	}

	/**
	 * Replaces a message from a user of a given length at a given index
	 * @param i Given index
	 * @param length Given length of how much to delete after the index
	 * @param msg Given message
	 */
	public void replace(int i, int length, String msg){
		
		del(i - 1, length - 1);
		insert(i - 1, msg);	
		state++;
	}
}