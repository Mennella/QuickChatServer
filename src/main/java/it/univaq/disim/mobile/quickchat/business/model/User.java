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
public class User {

    private int id;
    private String name;
    @JsonIgnore
    private String token;
    private String phone;
    @JsonIgnore
    private String urlImg;
    @JsonIgnore
    private Boolean active_push;
    @JsonIgnore
    private Set<Message> messages;
    @JsonIgnore
    private Date created_at;
    private Date updated_at;

    public User(int id, String name, String token, String phone, String urlImg, Boolean active_push, Set<Message> messages, Date created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.phone = phone;
        this.urlImg = urlImg;
        this.active_push = active_push;
        this.messages = messages;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public User() {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public Boolean getActive_push() {
        return active_push;
    }

    public void setActive_push(Boolean active_push) {
        this.active_push = active_push;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
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
