/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.business;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mennella
 */
public abstract class DBConnection {

    private static final String url = "jdbc:mysql://localhost:3307/serverdb";
    private static final String username = "root";
    private static final String password = "root";
    private static final String driver = "com.mysql.jdbc.Driver";

    protected Connection connection;
    protected Statement statement;
    protected PreparedStatement ps;
    protected ResultSet rs;

    protected static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

}
