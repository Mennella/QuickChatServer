/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.disim.mobile.quickchat.business.ChatroomService;
import it.univaq.disim.mobile.quickchat.business.MessageService;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCChatroomService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCMessageService;
import it.univaq.disim.mobile.quickchat.business.impl.JDBCUserService;
import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.Message;
import it.univaq.disim.mobile.quickchat.business.model.User;
import it.univaq.disim.mobile.quickchat.websocket.common.RequestMessage;
import it.univaq.disim.mobile.quickchat.websocket.common.ResponseMessage;
import it.univaq.disim.mobile.quickchat.websocket.common.UserSession;
import it.univaq.disim.mobile.quickchat.websocket.common.UserSessionHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Mennella
 */
@ServerEndpoint(value = "/quickchat/{token}")
public class CoreWebSocket {

    private static final UserSessionHandler sessionHandler = new UserSessionHandler();
    private final UserService userService = new JDBCUserService();
    private final ChatroomService chatroomService = new JDBCChatroomService();
    private final MessageService messageService = new JDBCMessageService();

    @OnClose
    public void onClose(Session session) {
        System.out.println("closed");
        sessionHandler.removeUserSession(session);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        System.out.println("opend");
        User user;
        user = userService.find(token);
        sessionHandler.addUserSession(session, user);
        System.out.println("user: " + user.getName());

    }

    @OnMessage
    public void onMessage(String payload, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RequestMessage rm = mapper.readValue(payload, RequestMessage.class);
            User owner = sessionHandler.getUserSession(session).getUser();
            Set<User> recipients = new HashSet<User>();
            ResponseMessage responseMessage;
            Chatroom c;
            Message m;
            switch (rm.getAction()) {
                case "message":
                    c = chatroomService.find(rm.getChat());
                    m = prepareMessage(rm, owner);
                    responseMessage = new ResponseMessage(rm.getAction(), c, m);
                    recipients = c != null ? c.getParticipants(): getUsers(rm.getUsers());
                    recipients = sendMessage(recipients, responseMessage, owner.getName() + " ti ha inviato un messaggio");
                    if (!recipients.isEmpty()) {
                        m.setRecipients(recipients);
                        messageService.saveMessage(m);
                    }
                    break;
                case "chatroom":
                    c = saveChat(owner, rm);
                    for (User recipient : c.getParticipants()) {
                        System.out.println(recipient.getId());
                    }
                    
                    responseMessage = new ResponseMessage("new chatroom", c, null);
                    sendMessage(c.getParticipants(), responseMessage, "Nuova chat di gruppo");
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(CoreWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CoreWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Message prepareMessage(RequestMessage rm, User owner) {
        Message m = new Message();
        m.setChatToken(rm.getChat());
        m.setOwner(owner);
        m.setText(rm.getText());
        m.setType(rm.getType());
        m.setUrlMedia(rm.getUrl_media());
        m.setCreated_at(rm.getCreated_at());

        return m;
    }

    private Set<User> getUsers(Set<Integer> su) throws Exception {
        Set<User> users = new HashSet<User>();
        for (Integer id : su) {
            User u = userService.find(id);
            if(u == null) throw new Exception("id non valido");
            users.add(u);
        }

        return users;
    }

    private Chatroom saveChat(User owner, RequestMessage rm) throws Exception {
        Set<User> participants = new HashSet<User>();
        participants.add(owner);
        participants.addAll(getUsers(rm.getUsers()));

        Chatroom c = new Chatroom();
        c.setName(rm.getText());
        c.setParticipants(participants);
        c.setUrlImg(rm.getUrl_media());
        c.setCreated_at(rm.getCreated_at());
        c.setUpdated_at(rm.getCreated_at());

        c = chatroomService.createChatroom(c);

        return c;
    }

    private Set<User> sendMessage(Set<User> participants, ResponseMessage responseMessage, String notification) {
        Set<User> users = new HashSet<User>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(responseMessage);
            for (User participant : participants) {
                UserSession userSession = sessionHandler.getUserSession(participant);
                if (userSession != null) {
                    userSession.getSession().getBasicRemote().sendText(response);
                } else {
//                    sendNotification(participant, notification);
                    users.add(participant);
                }
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CoreWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoreWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users;
    }

    private void sendNotification(User user, String message) {

        //manca filtro per num cell
        // manca codifica json message
        // cambiare codice basic per invio notifiche
        if (!user.getActive_push()) {
            return;
        }
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic YzIwZmI4MGItNzA4Zi00Y2UzLWFmZTgtNTM5MmI4Y2I1YWNi");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    + "\"app_id\": \"5eb5a37e-b458-11e3-ac11-000c2940e62c\","
                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"level\", \"relation\": \">\", \"value\": \"10\"},{\"operator\": \"OR\"},{\"field\": \"amount_spent\", \"relation\": \">\",\"value\": \"0\"}],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"English Message\"}"
                    + "}";

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
