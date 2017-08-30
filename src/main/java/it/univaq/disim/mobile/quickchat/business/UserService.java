/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business;

import it.univaq.disim.mobile.quickchat.business.model.User;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Mennella
 */
public interface UserService {

    public User find(String token);

    public User find(int id);

    public User findByPhone(String phone);

    public User create(User user);

    public void update(User user);

    public int genCode(String phone);

    public boolean validation(String phone, String code);

    public Set<User> getContact(Set<String> numbers, Date date);

    public boolean exists(String phone);

}
