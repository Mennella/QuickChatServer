/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public class Message {

    private int id;
    private String type;
    private String text;
    private String urlMedia;
    private String chatToken;
    private User owner;
    @JsonIgnore
    private Set<User> recipients;
    private Date created_at;

    public Message(int id, String type, String text, String urlMedia, String chatToken, User owner, Set<User> recipients, Date created_at) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.urlMedia = urlMedia;
        this.chatToken = chatToken;
        this.owner = owner;
        this.recipients = recipients;
        this.created_at = created_at;
    }

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUrlMedia() {
        return urlMedia;
    }

    public void setUrlMedia(String urlMedia) {
        this.urlMedia = urlMedia;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<User> recipients) {
        this.recipients = recipients;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

}
