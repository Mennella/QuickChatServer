/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business;


import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public interface ChatroomService {

    public Chatroom createChatroom(Chatroom c);

    public Chatroom find(String chat);

    public Set<Chatroom> getUpdatedChat(Date date, User user);
    
    public void addParticipant(User user, Chatroom chatroom);

    public void removeParticipant(User user, Chatroom c);

    public void update(Chatroom c);

}
