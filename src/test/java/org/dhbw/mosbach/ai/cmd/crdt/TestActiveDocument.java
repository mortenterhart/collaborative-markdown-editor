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
	
	@BeforeEach
	public void init() {
		
		Doc doc = new Doc();
		doc.setId(1);
		doc.setContent("Test content");
		
		activeDocument = new ActiveDocument();
		activeDocument.setDoc(doc);
		activeDocument.setState(0);
	}
	
	@Test
	public void testInsert() {

		activeDocument.insert(2, "abc");
		
		assertEquals(1, activeDocument.getState());
		assertEquals("Teabcst content", activeDocument.getDoc().getContent());
	}

	@Test
	public void testDel() {
		
		activeDocument.del(2, 3);
		
		assertEquals(1, activeDocument.getState());
		assertEquals("Tecontent", activeDocument.getDoc().getContent());
	}
}