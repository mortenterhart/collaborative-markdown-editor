package org.dhbw.mosbach.ai.cmd.crdt;

import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.websocket.Session;
import java.util.List;

/**
 * Wrapper class to keep a consistent document state and publish updates to all the connected clients
 *
 * @author 3040018
 */
public class MessageBroker {

    private static final Logger log = LoggerFactory.getLogger(MessageBroker.class);

    /**
     * Transforms a given message according to the provided MessageType
     *
     * @param msg            Given message
     * @param activeDocument Current active document
     */
    public void transform(Message msg, ActiveDocument activeDocument) {
        if (msg.getDocState() < activeDocument.getState()) {
            activeDocument.makeConsistent(msg, activeDocument.getState());
        }

        switch (msg.getMessageType()) {
            case Insert:
                activeDocument.insert(msg);
                break;
            case Delete:
                activeDocument.del(msg);
                break;
            case UserJoined: /*No transform needed for these message types*/
            case UserLeft:
            case ContentInit:
            case DocumentTitle:
            case UsersInit:
            case ChatMessage:
                break;
            default:
                throw new RuntimeException("Provided unsupported message type for transform.");
        }
    }

    /**
     * Creates a message if a user joined or left a document, so the other users can be notified.
     * Also creates a message to send the doc contents if a user just joined
     *
     * @param docId       Given document id
     * @param msg         Given message content depending on the type of system message
     * @param messageType Given message type
     * @return A message object
     */
    public Message createSystemMessage(int userId, int docId, int docState, String msg, MessageType messageType) {
        Message message = new Message();

        message.setCursorPos(-1);
        message.setDocState(docState);
        message.setDocId(docId);
        message.setMessageType(messageType);
        message.setMsg(msg);
        message.setUserId(userId);

        return message;
    }

    /**
     * Publishes the changes to the current document to the other clients
     *
     * @param msg            Given message
     * @param activeDocument Current active document
     */
    public void publishToOtherUsers(Message msg, ActiveDocument activeDocument, Session currentUserSession) {
        for (Session session : activeDocument.getUsers()) {
            try {
                if (session != currentUserSession) {
                    session.getBasicRemote().sendObject(msg);
                }
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Publishes a message to a single user only. Used to initially
     * send the doc contents to a user upon connecting
     *
     * @param msg     Given message
     * @param session Session of user to send the message to
     */
    public void publishToSingleUser(Message msg, Session session) {
        try {
            session.getBasicRemote().sendObject(msg);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Gets the list of active users on a doc and returns them as a JSON array
     *
     * @param users       Given active user within a doc
     * @param currentUser User requesting the list, to exclude himself
     * @return A list of user names formatted as a JSON array
     */
    public String getActiveUsers(List<Session> users, Session currentUser) {
        if (users.size() <= 1) { // Return empty array if the user is alone
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (Session session : users) {
            if (session != currentUser) {
                sb.append(formatUserMessage(session))
                  .append(",");
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }

    /**
     * Takes a single user session and formats a JSON message to provide user details
     *
     * @param session Given user session
     * @return A JSON object with the id and name of the user
     */
    public String formatUserMessage(Session session) {
        return Json.createObjectBuilder()
                   .add("id", session.getUserProperties().get(CmdConfig.SESSION_USERID).toString())
                   .add("name", session.getUserProperties().get(CmdConfig.SESSION_USERNAME).toString())
                   .add("imageUrl", "https://ui-avatars.com/api/?name=" + session.getUserProperties().get(CmdConfig.SESSION_USERNAME).toString() + "&background=0D8ABC&color=fff")
                   .build()
                   .toString();
    }
}
