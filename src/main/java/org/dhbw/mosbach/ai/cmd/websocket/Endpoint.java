package org.dhbw.mosbach.ai.cmd.websocket;

import org.dhbw.mosbach.ai.cmd.crdt.ActiveDocument;
import org.dhbw.mosbach.ai.cmd.crdt.Message;
import org.dhbw.mosbach.ai.cmd.crdt.MessageBroker;
import org.dhbw.mosbach.ai.cmd.db.DocDao;
import org.dhbw.mosbach.ai.cmd.db.HistoryDao;
import org.dhbw.mosbach.ai.cmd.db.UserDao;
import org.dhbw.mosbach.ai.cmd.model.Doc;
import org.dhbw.mosbach.ai.cmd.model.History;
import org.dhbw.mosbach.ai.cmd.security.Hashing;
import org.dhbw.mosbach.ai.cmd.util.CmdConfig;
import org.dhbw.mosbach.ai.cmd.util.MessageType;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * End point for the web socket communication between the client and the server
 *
 * @author 3040018
 */
@ServerEndpoint(value = "/ws/{docId}/{username}/{userId}", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class Endpoint {

    /**
     * Active docs being worked on by n users
     */
    private static Map<Integer, ActiveDocument> docs = Collections.synchronizedMap(new HashMap<>());

    @Inject
    private DocDao docDao;

    @Inject
    private UserDao userDao;

    @Inject
    private HistoryDao historyDao;

    @Inject
    private MessageBroker messageBroker;

    @Inject
    private Hashing hashing;

    /**
     * Gets triggered once a web socket connection is opened.
     *
     * @param docId    Given document id
     * @param username Given user name of current user
     * @param session  Current user session
     */
    @OnOpen
    public void onOpen(@PathParam("docId") int docId, @PathParam(CmdConfig.SESSION_USERNAME) String username, @PathParam(CmdConfig.SESSION_USERID) int userId, Session session) {
        session.getUserProperties().put(CmdConfig.SESSION_USERNAME, username);
        session.getUserProperties().put(CmdConfig.SESSION_USERID, userId);

        Doc doc = null;

        if (docs.get(docId) == null) {
            doc = docDao.getDoc(docId);
            if (doc == null) {
                Message wrongDocIdMsg = messageBroker.createSystemMessage(userId, docId, -1, String.valueOf(docId), MessageType.WrongDocId);
                messageBroker.publishToSingleUser(wrongDocIdMsg, session);
                return;
            }
            docs.put(docId, new ActiveDocument(doc, 0));
        }

        if (doc == null) {
            doc = docs.get(docId).getDoc();
        }

        if (doc.getContent() == null) {
            docs.get(docId).getDoc().setContent("");
        }

        docs.get(docId).getUsers().add(session);

        Message contentInitMsg = messageBroker.createSystemMessage(userId, docId, docs.get(docId).getState(), doc.getContent(), MessageType.ContentInit);
        messageBroker.publishToSingleUser(contentInitMsg, session);

        Message documentTitleMsg = messageBroker.createSystemMessage(userId, docId, -1, doc.getName(), MessageType.DocumentTitle);
        messageBroker.publishToSingleUser(documentTitleMsg, session);

        Message userInitMsg = messageBroker.createSystemMessage(userId, docId, -1, messageBroker.getActiveUsers(docs.get(docId).getUsers(), session), MessageType.UsersInit);
        messageBroker.publishToSingleUser(userInitMsg, session);

        Message userJoinedMsg = messageBroker.createSystemMessage(userId, docId, -1, messageBroker.formatUserMessage(session), MessageType.UserJoined);
        messageBroker.publishToOtherUsers(userJoinedMsg, docs.get(docId), session);
    }

    /**
     * Gets triggered once a message is sent from the client.<br>
     *
     * @param msg     Given message
     * @param session Current user session
     */
    @OnMessage
    public void onMessage(Message msg, Session session) {
        ActiveDocument currentDoc = docs.get(msg.getDocId());

        messageBroker.transform(msg, currentDoc);
        messageBroker.publishToOtherUsers(msg, currentDoc, session);
    }

    /**
     * Gets triggered once a user closes the connection by logging off or closing the browser tab etc.
     * If it is the last user in the doc, the current state of the doc gets saved into the database.
     *
     * @param session Current user session
     */
    @OnClose
    public void onClose(Session session) {
        for (int docId : docs.keySet()) {
            for (Session singleUserSession : docs.get(docId).getUsers()) {
                if (singleUserSession.equals(session)) {

                    docs.get(docId).getUsers().remove(singleUserSession);

                    int userId = (int) singleUserSession.getUserProperties().get(CmdConfig.SESSION_USERID);

                    Message userLeftMsg = messageBroker.createSystemMessage(userId, docId, -1, messageBroker.formatUserMessage(singleUserSession), MessageType.UserLeft);
                    messageBroker.publishToOtherUsers(userLeftMsg, docs.get(docId), session);

                    break;
                }
            }

            if (docs.get(docId).getUsers().isEmpty()) {

                // Save current doc from db to history
                Doc currentDocState = docDao.getDoc(docId);
                History history = new History();
                history.setContent(currentDocState.getContent());
                history.setDoc(currentDocState);
                history.setHash(hashing.hashDocContent(history.getContent()));
                historyDao.createHistory(history);

                // Save current doc from client to db
                Doc activeDoc = docs.get(docId).getDoc();
                activeDoc.setUuser(userDao.getUserByName(session.getUserProperties().get(CmdConfig.SESSION_USERNAME).toString()));
                docDao.updateDoc(activeDoc);

                docs.remove(docId);
            }
        }
    }

    /**
     * Gets triggered on an error.
     *
     * @param T Given Exception
     */
    @OnError
    public void onError(Throwable T) {
        T.printStackTrace();
    }
}
