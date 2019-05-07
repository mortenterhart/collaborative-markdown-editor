package org.dhbw.mosbach.ai.cmd.websocket;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.dhbw.mosbach.ai.cmd.crdt.Message;
import org.dhbw.mosbach.ai.cmd.util.MessageType;

/**
 * Decoder class for messages to use in the web socket endpoint
 * @author 3040018
 *
 */
public class MessageDecoder implements Decoder.Text<Message>{

	@Override
	public void init(EndpointConfig config) {}

	@Override
	public void destroy() {}

	@Override
	public Message decode(String jsonMessage) throws DecodeException {

		JsonObject jsonMsg = Json.createReader(new StringReader(jsonMessage)).readObject();
		
		Message message = new Message();
		
		message.setUserId(jsonMsg.getInt("userId", -1));
		message.setDocId(jsonMsg.getInt("docId", -1));
		message.setCursorPos(jsonMsg.getInt("cursorPos", 0));
		message.setMsg(jsonMsg.getString("msg")  == null ? "" : jsonMsg.getString("msg"));
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