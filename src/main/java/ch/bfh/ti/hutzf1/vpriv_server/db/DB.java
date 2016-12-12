/*
 * VPriv Client Server Simulator
 * Copyright 2017 Fabian Hutzli
 * Berner Fachhochschule
 *
 * All rights reserved.
 */
package ch.bfh.ti.hutzf1.vpriv_server.db;

import ch.bfh.ti.hutzf1.vpriv_server.log.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Handles the connection to SQL DB.
 * @author Fabian Hutzli
 */

public class DB {
    // Connect to database.
    private final String CONNECTIONSTRING = 
        "jdbc:sqlserver://btdev.database.windows.net:1433;"
        + "database=btdev;"
        + "user=btdev@btdev;"
        + "password=abcd1234$$$$;"
        + "encrypt=true;"
        + "trustServerCertificate=false;"
        + "hostNameInCertificate=*.database.windows.net;"
        + "loginTimeout=30;";
    
    // Declare the JDBC objects.  
    private Connection connection = null;
    private final Log LOG;
    
    public DB(Log log) {
        this.LOG = log;
    }
    
    /**
     *
     */
    
    public void connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(CONNECTIONSTRING);  
        } catch (ClassNotFoundException | SQLException ex) {
            LOG.exception(ex);
        }
    }
    
    /**
     *
     */
    
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
    }
    
    /**
     *
     * @param query
     */
    
    public void execute(String query) {
        try {
            Statement stmt = connection.createStatement();  
            stmt.execute(query);
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
    }
    
    /**
     *
     * @param query
     * @return
     */
    
    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);  
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
        return rs;
    }
}
