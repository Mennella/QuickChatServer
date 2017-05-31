/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.websocket.common;

import it.univaq.disim.mobile.quickchat.business.model.User;
import javax.websocket.Session;

/**
 *
 * @author Mennella
 */
public class UserSession {

    private Session session;
    private User user;

    public UserSession(Session session, User user) {
        this.session = session;
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}

