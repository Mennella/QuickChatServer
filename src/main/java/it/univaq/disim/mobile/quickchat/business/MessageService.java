/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

*/
package it.univaq.disim.mobile.quickchat.business;

import it.univaq.disim.mobile.quickchat.business.model.Message;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public interface MessageService {

    public void saveMessage(Message message);
    
    public Set<Message> getMessages (User recipient);
    
    public void removeRecipient (User recipient);
    
    public void removeMessages();
    
}
