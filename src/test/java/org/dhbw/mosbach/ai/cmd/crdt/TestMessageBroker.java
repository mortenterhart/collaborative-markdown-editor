package org.dhbw.mosbach.ai.cmd.crdt;

import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.util.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing the MessageBroker class
 *
 * @author 3040018
 */
public class TestMessageBroker {

    private MessageBroker messageBroker;
    private ActiveDocument activeDocument;
    private Message msg;

    @BeforeEach
    public void init() {
        messageBroker = new MessageBroker();

        Doc doc = new Doc();
        doc.setId(1);
        doc.setContent("Test content");

        activeDocument = new ActiveDocument();
        activeDocument.setDoc(doc);
        activeDocument.setState(0);

        msg = new Message();
        msg.setCursorPos(2);
        msg.setMsg("abc");
    }

    @Test
    public void testTransformInsert() {
        msg.setMessageType(MessageType.Insert);

        messageBroker.transform(msg, activeDocument);

        assertEquals(1, activeDocument.getState());
        assertEquals("Teabcst content", activeDocument.getDoc().getContent());
    }

    @Test
    public void testTransformDel() {
        msg.setMessageType(MessageType.Delete);

        messageBroker.transform(msg, activeDocument);

        assertEquals(1, activeDocument.getState());
        assertEquals("Tecontent", activeDocument.getDoc().getContent());
    }

    @Test
    public void testTransformNoTransform() {
        msg.setMessageType(MessageType.ChatMessage);

        messageBroker.transform(msg, activeDocument);

        assertEquals(0, activeDocument.getState());
        assertEquals("Test content", activeDocument.getDoc().getContent());
    }
}
