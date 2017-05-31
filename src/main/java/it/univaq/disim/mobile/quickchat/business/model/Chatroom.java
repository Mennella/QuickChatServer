/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business.model;

import java.util.Date;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public class Chatroom {

    private int id;
    private String name;
    private String urlImg;
    private String token;
    private Set<User> participants;
    private Date created_at;
    private Date updated_at;

    public Chatroom(int id, String name, String urlImg, String token, Set<User> participants, Date created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.urlImg = urlImg;
        this.token = token;
        this.participants = participants;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Chatroom() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

}
