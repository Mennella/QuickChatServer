/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.websocket.common;

import java.util.Date;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public class RequestMessage {

    private String action;
    private String type;
    private String text;
    private String url_media;
    private Set<Integer> users;
    private String chat;
    private Date created_at;

    public RequestMessage() {
    }

    public RequestMessage(String action, String type, String text, String url_media, Set<Integer> users, String chat, Date created_at) {
        this.action = action;
        this.type = type;
        this.text = text;
        this.url_media = url_media;
        this.users = users;
        this.chat = chat;
        this.created_at = created_at;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl_media() {
        return url_media;
    }

    public void setUrl_media(String url_media) {
        this.url_media = url_media;
    }

    public Set<Integer> getUsers() {
        return users;
    }

    public void setUsers(Set<Integer> users) {
        this.users = users;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

}
