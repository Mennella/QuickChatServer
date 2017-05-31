/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.websocket.common;

import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.Message;

/**
 *
 * @author Mennella
 */
public class ResponseMessage {

    private String action;
    private Chatroom chat;
    private Message message;

    public ResponseMessage(String action, Chatroom chat, Message message) {
        this.action = action;
        this.chat = chat;
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Chatroom getChat() {
        return chat;
    }

    public void setChat(Chatroom chat) {
        this.chat = chat;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
