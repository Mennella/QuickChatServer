/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business.impl;

import it.univaq.disim.mobile.quickchat.business.ChatroomService;
import it.univaq.disim.mobile.quickchat.business.DBConnection;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.model.Chatroom;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mennella
 */
public class JDBCChatroomService extends DBConnection implements ChatroomService {

    private UserService userService = new JDBCUserService();

    @Override
    public Chatroom createChatroom(Chatroom c) {

            PreparedStatement preparedStatement = null;
        try {
            this.connection = getConnection();
            connection.setAutoCommit(false);

//            c.setToken(createToken());
            String sql = "INSERT INTO chatrooms ( name, url_img, token, created_at) VALUES (?,?,?,?)";
            this.ps = connection.prepareStatement(sql);
            ps.setString(1, c.getName());
            ps.setString(2, c.getUrlImg());
            ps.setString(3, c.getToken());
            ps.setTimestamp(4, new java.sql.Timestamp(c.getCreated_at().getTime()));
            ps.executeUpdate();

            for (User u : c.getParticipants()) {
                String sql1 = "INSERT INTO participants ( chat_token, user_id) VALUES (?,?)";
                preparedStatement = connection.prepareStatement(sql1);
                preparedStatement.setString(1, c.getToken());
                preparedStatement.setInt(2, u.getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ps.close();
                if(preparedStatement != null){
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return find(c.getToken());
    }

    @Override
    public Chatroom find(String token) {
        Chatroom chatroom = null;
        try {
            this.connection = getConnection();
            this.statement = connection.createStatement();
            ResultSet resultSet;

            rs = statement.executeQuery("SELECT * FROM chatrooms WHERE token = '" + token + "'");
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String urlImg = rs.getString("url_img");
                Date created_at = new java.util.Date(rs.getTimestamp("created_at").getTime());
                Date updated_at = new java.util.Date(rs.getTimestamp("updated_at").getTime());

                resultSet = statement.executeQuery("SELECT * FROM participants WHERE participants.chat_token = '" + token + "'");

                chatroom = new Chatroom(id, name, urlImg, token, new HashSet<User>(), created_at, updated_at);
                while (resultSet.next()) {
                    User u = userService.find(resultSet.getInt("user_id"));
                    chatroom.getParticipants().add(u);
                }

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
        return chatroom;
    }

    private String createToken() throws SQLException {
        Chatroom c = null;
        String token;
        while (true) {
            token = java.util.UUID.randomUUID().toString();
            statement = connection.createStatement();
            String query = "SELECT c.token FROM chatrooms c WHERE c.token = '" + token + "'";
            rs = statement.executeQuery(query);
            if (!rs.next()) {
                return token;
            }
        }
    }

    @Override
    public Set<Chatroom> getUpdatedChat(Date date, User user) {

        Set<Chatroom> chatrooms = new HashSet<Chatroom>();
        ResultSet result = null;
        Set<User> participants;
        Chatroom c;
        try {
            this.connection = getConnection();
            statement = connection.createStatement();
            Statement s = connection.createStatement();
            ps = connection.prepareStatement("SELECT c.* FROM chatrooms as c, participants as p WHERE p.chat_token = c.token AND p.user_id = ? AND c.updated_at >= ? ");
            ps.setInt(1, user.getId());
            ps.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
            rs = ps.executeQuery();
//            rs = statement.executeQuery("SELECT c.* FROM chatrooms as c, participants as p WHERE p.chat_token = c.token AND p.user_id = " + user.getId() + " AND c.updated_at >= " + new java.sql.Timestamp(date.getTime()));
            while (rs.next()) {
                participants = new HashSet<User>();
                c = new Chatroom();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setToken(rs.getString("token"));
                c.setUrlImg(rs.getString("url_img"));
                c.setCreated_at(new java.util.Date(rs.getTimestamp("created_at").getTime()));
                c.setUpdated_at(new java.util.Date(rs.getTimestamp("updated_at").getTime()));

                result = s.executeQuery("SELECT * FROM participants WHERE chat_token = '" + c.getToken() + "'");
                while (result.next()) {
                    User u = userService.find(result.getInt("user_id"));
                    participants.add(u);
                }
                c.setParticipants(participants);
                chatrooms.add(c);

            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (result != null) {
                    result.close();
                }
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return chatrooms;
    }

    @Override
    public void addParticipant(User user, Chatroom chatroom) {
        ResultSet result = null;
        try {
            this.connection = getConnection();
            ps = connection.prepareStatement("INSERT INTO participants (user_id, chat_token) VALUES (?,?)");
            ps.setInt(1, user.getId());
            ps.setString(2, chatroom.getToken());
            ps.executeUpdate();
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
    public void removeParticipant(User user, Chatroom c) {
        try {
            this.connection = getConnection();
            ps = connection.prepareStatement("DELETE FROM participants WHERE user_id = ? AND chat_token = ?");
            ps.setInt(1, user.getId());
            ps.setString(2, c.getToken());
            ps.executeUpdate();
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
    public void update(Chatroom c) {
        try {
            this.connection = getConnection();
            ps = connection.prepareStatement("UPDATE chatrooms SET name = ?, url_img = ? WHERE id = ? ");
            ps.setString(1, c.getName());
            ps.setString(2, c.getUrlImg());
            ps.setInt(3, c.getId());
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

}
