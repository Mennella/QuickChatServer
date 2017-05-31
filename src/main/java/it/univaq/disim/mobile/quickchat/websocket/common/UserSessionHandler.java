/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.websocket.common;


import it.univaq.disim.mobile.quickchat.business.model.User;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;

/**
 *
 * @author Mennella
 */
public class UserSessionHandler {

    public UserSessionHandler() {
    }

    private final Set<UserSession> userSession = new HashSet<>();

    public void addUserSession(Session session, User user) {
        UserSession us = new UserSession(session, user);
        this.userSession.add(us);
    }

    public void removeUserSession(Session session) {
        UserSession us = getUserSession(session);
        this.userSession.remove(us);
    }

    public void removeUserSession(User user) {
        UserSession us = getUserSession(user);
        this.userSession.remove(us);
    }

    public UserSession getUserSession(Session session) {
        for (UserSession user : this.userSession) {
            if ((user.getSession().getId()).equals(session.getId())) {
                return user;
            }
        }

        return null;
    }

    public UserSession getUserSession(User user) {
        for (UserSession us : this.userSession) {
            if (us.getUser().getId() == user.getId()) {
                return us;
            }
        }

        return null;
    }

    public Set<UserSession> getUserSession() {
        return this.userSession;
    }

    public void printSetUserSession() {
        for (UserSession user : this.userSession) {
            System.out.println("session id= " + user.getSession().getId() + " user id= " + user.getUser().getId());
        }
    }

}
