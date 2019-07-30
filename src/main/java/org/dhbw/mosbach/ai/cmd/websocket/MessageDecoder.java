package org.dhbw.mosbach.ai.cmd.websocket;

import org.dhbw.mosbach.ai.cmd.crdt.Message;
import org.dhbw.mosbach.ai.cmd.util.MessageType;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;

/**
 * Decoder class for messages to use in the web socket endpoint
 *
 * @author 3040018
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Message decode(String jsonMessage) throws DecodeException {
        JsonObject jsonMsg = Json.createReader(new StringReader(jsonMessage)).readObject();

        Message message = new Message();

        message.setUserId(jsonMsg.getInt("userId"));
        message.setDocId(jsonMsg.getInt("docId"));
        message.setCursorPos(jsonMsg.getInt("cursorPos"));
        message.setDocState(jsonMsg.getInt("docState"));
        message.setMsg(jsonMsg.getString("msg") == null ? "" : jsonMsg.getString("msg"));
        message.setMessageType(MessageType.valueOf(jsonMsg.getString("messageType")));

        return message;
    }

    @Override
    public boolean willDecode(String jsonMessage) {
        try {
            Json.createReader(new StringReader(jsonMessage)).readObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
