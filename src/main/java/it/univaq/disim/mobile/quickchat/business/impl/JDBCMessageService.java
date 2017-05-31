/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.univaq.disim.mobile.quickchat.business.impl;
import it.univaq.disim.mobile.quickchat.business.DBConnection;
import it.univaq.disim.mobile.quickchat.business.MessageService;
import it.univaq.disim.mobile.quickchat.business.UserService;
import it.univaq.disim.mobile.quickchat.business.model.Message;
import it.univaq.disim.mobile.quickchat.business.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mennella
 */
public class JDBCMessageService extends DBConnection implements MessageService {

    private final UserService userService = new JDBCUserService();

    @Override
    public void saveMessage(Message message) {
            PreparedStatement preparedStatement = null;
        try {
            this.connection = getConnection();
            connection.setAutoCommit(false);

            String sql = "INSERT INTO messages ( type, text, url_media, chat_token, owner_id, created_at) VALUES (?,?,?,?,?,?)";
            this.ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getType());
            ps.setString(2, message.getText());
            ps.setString(3, message.getUrlMedia());
            ps.setString(4, message.getChatToken());
            ps.setInt(5, message.getOwner().getId());
            ps.setDate(6, new java.sql.Date(message.getCreated_at().getTime()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            rs.next();

            int last_inserted_id = rs.getInt(1);

            for (User u : message.getRecipients()) {
                String sql1 = "INSERT INTO recipients ( message_id, user_id) VALUES (?,?)";
                preparedStatement = connection.prepareStatement(sql1);
                preparedStatement.setInt(1, last_inserted_id);
                preparedStatement.setInt(2, u.getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                ps.close();
                if(preparedStatement != null){
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Set<Message> getMessages(User recipient) {
        Set<Message> messages = new HashSet<Message>();
        ResultSet result = null;
        Set<User> recipients;
        Message m;
        try {
            this.connection = getConnection();
            statement = connection.createStatement();
            Statement s = connection.createStatement();
            rs = statement.executeQuery("SELECT messages.* FROM messages, recipients WHERE messages.id = recipients.message_id AND recipients.user_id = " + recipient.getId());
            while (rs.next()) {
                recipients = new HashSet<User>();
                m = new Message();
                m.setId(rs.getInt("id"));
                m.setText(rs.getString("text"));
                m.setType(rs.getString("type"));
                m.setChatToken(rs.getString("chat_token"));
                m.setUrlMedia(rs.getString("url_media"));
                m.setOwner(userService.find(rs.getInt("owner_id")));
                m.setCreated_at(new java.util.Date(rs.getDate("created_at").getTime()));

                result = s.executeQuery("SELECT * FROM recipients WHERE message_id = " + m.getId());
                while (result.next()) {
                    User u = userService.find(result.getInt("user_id"));
                    recipients.add(u);
                }
                m.setRecipients(recipients);
                messages.add(m);

            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                if (result != null) {
                    result.close();
                }
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return messages;
    }

    @Override
    public void removeRecipient(User recipient) {
        try {
            this.connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM recipients WHERE user_id = " + recipient.getId());
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void removeMessages() {
        try {
            this.connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM messages WHERE id NOT IN (SELECT recipients.message_id FROM recipients GROUP BY recipients.message_id)");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCMessageService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
