/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.ti.hutzf1.vpriv_server.db;

import java.sql.SQLException;

/**
 *
 * @author fh
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        DB db = new DB();
        db.connect();
        db.disconnect();
    }
    
}
