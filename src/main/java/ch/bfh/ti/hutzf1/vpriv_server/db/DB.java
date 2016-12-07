package ch.bfh.ti.hutzf1.vpriv_server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fabian Hutzli
 */

public class DB {
    // Connect to database.
    String connectionString = 
        "jdbc:sqlserver://btdev.database.windows.net:1433;"
        + "database=btdev;"
        + "user=btdev@btdev;"
        + "password=abcd1234$$$$;"
        + "encrypt=true;"
        + "trustServerCertificate=false;"
        + "hostNameInCertificate=*.database.windows.net;"
        + "loginTimeout=30;";
    
    // Declare the JDBC objects.  
    Connection connection = null;   
    
    /**
     *
     */
    
    public void connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionString);  
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     */
    
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
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
            Statement stmt = connection.createStatement();  
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
}
