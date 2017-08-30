/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business.impl;

import it.univaq.disim.mobile.quickchat.business.DBConnection;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.model.Message;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mennella
 */
public class JDBCUserService extends DBConnection implements UserService {

    @Override
    public boolean exists(String phone) {
        User user = null;
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();

            rs = statement.executeQuery("SELECT * FROM users WHERE phone = '" + phone + "'");
            if (rs.next()) {
                return true;
            }
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    @Override
    public User find(String token) {
        User user = null;
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();

            rs = statement.executeQuery("SELECT * FROM users WHERE token = '" + token + "'");
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String urlImg = rs.getString("url_img");
                Boolean active_push = rs.getBoolean("active_push");
                Set<Message> messages = new HashSet<Message>();
                Date created_at = new java.util.Date(rs.getTimestamp("created_at").getTime());
                Date updated_at = new java.util.Date(rs.getTimestamp("updated_at").getTime());

                user = new User(id, name, token, phone, urlImg, active_push, messages, created_at, updated_at);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return user;
    }

    @Override
    public User find(int id) {
        User user = null;
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM users WHERE id = " + id);
            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String urlImg = rs.getString("url_img");
                Boolean active_push = rs.getBoolean("active_push");
                Set<Message> messages = new HashSet<Message>();
                String token = rs.getString("token");
                Date created_at = new java.util.Date(rs.getTimestamp("created_at").getTime());
                Date updated_at = new java.util.Date(rs.getTimestamp("updated_at").getTime());
                user = new User(id, name, token, phone, urlImg, active_push, messages, created_at, updated_at);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("trovato: " + user.getName());
        return user;
    }
    
    @Override
    public User findByPhone(String phone) {
        User user = null;
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM users WHERE phone = " + phone);
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String urlImg = rs.getString("url_img");
                Boolean active_push = rs.getBoolean("active_push");
                Set<Message> messages = new HashSet<Message>();
                String token = rs.getString("token");
                Date created_at = new java.util.Date(rs.getTimestamp("created_at").getTime());
                Date updated_at = new java.util.Date(rs.getTimestamp("updated_at").getTime());
                user = new User(id, name, token, phone, urlImg, active_push, messages, created_at, updated_at);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("trovato: " + user.getName());
        return user;
    }
    
@Override
    public User create(User user) {
        try {
            this.connection = getConnection();
            ps = connection.prepareStatement("INSERT INTO users ( name,token, phone, url_img, created_at) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, createToken());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUrlImg());
            ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            rs.next();

            int last_inserted_id = rs.getInt(1);

            user = find(last_inserted_id);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ps.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return user;
    }

    private String createToken() throws SQLException {
        User u = null;
        String token;
        while (true) {
            token = java.util.UUID.randomUUID().toString();
            statement = connection.createStatement();
            String query = "SELECT u.token FROM users u WHERE u.token = '" + token + "'";
            rs = statement.executeQuery(query);
            if (!rs.next()) {
                return token;
            }
        }
    }

    @Override
    public void update(User user) {
        try {
            this.connection = getConnection();
            ps = connection.prepareStatement("UPDATE users SET name = ?, url_img = ? WHERE id = ? ");
            ps.setString(1, user.getName());
            ps.setString(2, user.getUrlImg());
            ps.setInt(3, user.getId());
            ps.executeUpdate();
            System.out.println("modificato");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public int genCode(String phone) {
        Random r = new Random();
        int code = r.nextInt(100000);
        try {
            this.connection = getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM codes WHERE phone = '" + phone + "'");
            if (rs.next()) {
                System.out.println("esiste");
                ps = connection.prepareStatement("UPDATE codes SET code = ?  WHERE phone = ? ");
            } else {
                System.out.println("non esiste");
                ps = connection.prepareStatement("INSERT INTO codes (code,phone) VALUES (?,?)");
            }
            ps.setString(1, String.valueOf(code));
            ps.setString(2, phone);
            ps.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ps.close();
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return code;
    }

    @Override
    public boolean validation(String phone, String code) {
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();
            System.out.println("code: " + code + " phone: " + phone);
            rs = statement.executeQuery("SELECT * FROM codes WHERE phone= " + phone + " AND code = " + code);
            if (rs.next()) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                rs.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public Set<User> getContact(Set<String> numbers, Date date) {
        Set<User> users = new HashSet<User>();
        Statement st = null;
        PreparedStatement pres;
        ResultSet result = null;
        try {
            this.connection = getConnection();
            st = connection.createStatement();
            for (String number : numbers) {
                System.out.println("numner: " + number);
                if(date == null){
                    System.out.println("null");
                    result = st.executeQuery("SELECT id FROM users WHERE phone = " + number);
                }else{
                    System.out.println("not null");
                    System.out.println("not null" + new Timestamp(date.getTime()));
                    result = st.executeQuery("SELECT id FROM users WHERE phone = " + number + " AND updated_at >= '" + new Timestamp(date.getTime()) +"'");
                }
                if (result.next()) {
//                    User u = new User();
//                    u.setId(result.getInt("id"));
                    User u = find(result.getInt("id"));
                    System.out.println("fdsfsdf");
                    users.add(u);
                }
//                statement.close();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                st.close();
                result.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return users;
    }

}
