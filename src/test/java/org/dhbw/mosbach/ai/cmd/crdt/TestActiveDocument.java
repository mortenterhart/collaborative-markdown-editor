package org.dhbw.mosbach.ai.cmd.crdt;

import static org.junit.jupiter.api.Assertions.*;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing the ActiveDocument class
 * @author 3040018
 */
public class TestActiveDocument {
	
	private ActiveDocument activeDocument;
	private Message msg;
	
	@BeforeEach
	public void init() {
		
		Doc doc = new Doc();
		doc.setId(1);
		doc.setContent("Test content");
		
		msg = new Message();
		msg.setCursorPos(2);
		msg.setMsg("abc");
		
		activeDocument = new ActiveDocument();
		activeDocument.setDoc(doc);
		activeDocument.setState(0);
	}
	
	@Test
	public void testInsert() {

		activeDocument.insert(msg);
		
		assertEquals(1, activeDocument.getState());
		assertEquals("Teabcst content", activeDocument.getDoc().getContent());
	}

	@Test
	public void testDel() {
		
		activeDocument.del(msg);
		
		assertEquals(1, activeDocument.getState());
		assertEquals("Tecontent", activeDocument.getDoc().getContent());
	}
}