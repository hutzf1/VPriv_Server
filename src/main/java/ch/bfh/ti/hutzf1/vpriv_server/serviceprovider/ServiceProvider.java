package ch.bfh.ti.hutzf1.vpriv_server.serviceprovider;

import ch.bfh.ti.hutzf1.vpriv_server.crypto.OneWayFunction;
import ch.bfh.ti.hutzf1.vpriv_server.crypto.PedersenScheme;
import ch.bfh.ti.hutzf1.vpriv_server.db.DB;
import ch.bfh.ti.hutzf1.vpriv_server.log.Log;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.json.JSONObject;

/**
 * Defines the Service Provider and all its functions.
 * @author Fabian Hutzli
 */

public class ServiceProvider {
    
    private final PedersenScheme PS;
    private final Log LOG;
    private final OneWayFunction HASH;
    private final DB DB;

    /**
     *
     */
    
    public ServiceProvider() {
        this.LOG = new Log();
        this.PS = new PedersenScheme(LOG);
        this.HASH =  new OneWayFunction(PS);
        this.DB = new DB(LOG);
    }
    
    private void cleanUp() {
        DB.execute("DELETE FROM Devices");
        DB.execute("DBCC CHECKIDENT (Devices, RESEED, 0)");
        DB.execute("DELETE FROM Toll");
        DB.execute("DBCC CHECKIDENT (Toll, RESEED, 0)");
        DB.execute("DELETE FROM Tags");
        DB.execute("DBCC CHECKIDENT (Tags, RESEED, 0)");
        DB.execute("DELETE FROM RoundKeys");
        DB.execute("DBCC CHECKIDENT (RoundKeys, RESEED, 0)");
        DB.execute("DELETE FROM Round");
        DB.execute("DBCC CHECKIDENT (Round, RESEED, 0)");
        DB.execute("DELETE FROM DrivingRecords");
        DB.execute("DBCC CHECKIDENT (DrivingRecords, RESEED, 0)");
        DB.execute("DELETE FROM PermutedRecords");
        DB.execute("DBCC CHECKIDENT (PermutedRecords, RESEED, 0)");
    }
    
    private int getDeviceId(String DeviceIdentity) {
        int deviceId = 0;
        
        try {
            LOG.console("SELECT ID FROM Devices WHERE DeviceIdentity = N'" + DeviceIdentity + "';");
            
            ResultSet rs = DB.executeQuery("SELECT ID FROM Devices WHERE DeviceIdentity = N'" + DeviceIdentity + "'");
            
            while (rs.next()) {
                deviceId = rs.getInt("ID");    
            }
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
        
        return deviceId;
    }
    
    /**
     *
     * @param jo
     */
    
    public void putRoundPackage(JSONObject jo) {
        DB.connect();
        
        if(jo.getInt("round") == 0) {
            cleanUp();
            LOG.console("INSERT INTO Devices VALUES (N'" + jo.getString("id") + "');");
            DB.execute("INSERT INTO Devices VALUES (N'" + jo.getString("id") + "');");
        }
        
        int getId = this.getDeviceId(jo.getString("id"));
        
        for(int i = 0; i < jo.length()-3; i++) {
            LOG.console("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("v" + i) + "');");
            DB.execute("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("v" + i) + "');");
        }
        
        LOG.console("INSERT INTO RoundKeys VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("key") + "');");
        DB.execute("INSERT INTO RoundKeys VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("key") + "');");
        
        DB.disconnect();
    }
    
    /**
     *
     * @param jo
     */
    
    public void putDrivingData(JSONObject jo) {
        Random rand = new Random();
        int toll = rand.nextInt(10);
        
        DB.connect();

        LOG.console("INSERT INTO DrivingRecords VALUES (N'" + jo.getBigInteger("tag") + "', " + jo.getInt("longitude") + ", " + jo.getInt("latitude") + ", N'" + jo.getString("timestamp") + "', " + toll + ");");
        DB.execute("INSERT INTO DrivingRecords VALUES (N'" + jo.getBigInteger("tag") + "', " + jo.getInt("longitude") + ", " + jo.getInt("latitude") + ", N'" + jo.getString("timestamp") + "', " + toll + ");");  
        
        DB.disconnect();
    }    
    
    /**
     *
     * @param jo
     */
    
    public void putCostData(JSONObject jo) {
        DB.connect();
        
        int getId = this.getDeviceId(jo.getString("id"));
        
        LOG.console("INSERT INTO Round VALUES (" + getId + ", 0, " + jo.getInt("cost") + ");");
        DB.execute("INSERT INTO Round VALUES (" + getId + ", 0, " + jo.getInt("cost") + ");");
        
        DB.disconnect();
    }

    /**
     *
     * @param jo
     */
    
    public void putPermutedPackage(JSONObject jo) {
        DB.connect();
        
        int getId = this.getDeviceId(jo.getString("id"));
        
        for(int x = 1; x <= (jo.length() - 2) / 2; x++) {
            LOG.console("INSERT INTO PermutedRecords VALUES (" + getId + ", " + jo.getInt("U") + ", N'" + jo.getBigInteger("w" + x) + "', N'" + jo.getBigInteger("c" + x) + "');");
            DB.execute("INSERT INTO PermutedRecords VALUES (" + getId + ", " + jo.getInt("U") + ", N'" + jo.getBigInteger("w" + x) + "', N'" + jo.getBigInteger("c" + x) + "');");
        }
        
        DB.disconnect();
    }
    
    /**
     *
     * @return
     */
     
    public JSONObject getControlMethod() {
        JSONObject jo = new JSONObject();
        Random rand = new Random();
        //jo.put("bi", rand.nextInt(2));
        jo.put("bi", 0);
        return jo;
    }
    
    public JSONObject getAllData() {
        JSONObject jo = new JSONObject();
        
        DB.connect();
        
        ResultSet rs = null;
        int i = 1;
     
        try {
            rs = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords;");
            while (rs.next()) {
                jo.put("w" + i, rs.getString("Tag"));
                LOG.console(rs.getString("Tag"));
                jo.put("c" + i, rs.getInt("Cost"));
                LOG.console(rs.getString("Cost"));
                i++;
            }
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
        
        DB.disconnect();
        
        return jo;
    }
    
    /**
     *
     * @param jo
     */
    
    public void putControlData0(JSONObject jo) {
        this.LOG.console("///// START RECON PHASE 0");
        LOG.console(jo.toString());
        DB.connect();

        int cost = 0;
        int getId = this.getDeviceId(jo.getString("id"));

        BigInteger openingKey = jo.getBigInteger("dki");
        BigInteger getKey = null;   
        
        ResultSet rsKey = DB.executeQuery("SELECT RoundKey FROM RoundKeys WHERE DeviceId = N'" + getId + "'");
        
        try {
            while (rsKey.next()) {
                getKey = BigInteger.valueOf(rsKey.getLong("RoundKey"));
            }
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
        
        //this.LOG.console(getKey.toString());
        //this.LOG.console(jo.getBigInteger("dki").toString());
       
        BigInteger ki = this.PS.decommit(getKey, openingKey);        
        this.LOG.console(ki.toString());
            
        ResultSet rsDrivingRecords = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords");
        ResultSet rsPermutedRecords = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE DeviceId = N'" + getId + "'");
        
        try {
            while (rsPermutedRecords.next()) {
                while (rsDrivingRecords.next()) {
                    BigInteger dr = this.HASH.getHash(new BigInteger(rsDrivingRecords.getString("Tag")), ki);
                    BigInteger pr = new BigInteger(rsPermutedRecords.getString("Tag"));
                    this.LOG.console(dr.toString());
                    this.LOG.console(pr.toString());
                    if(dr.equals(pr)){
                        this.LOG.console(dr + " == " + pr);
                        BigInteger costDr = new BigInteger(rsDrivingRecords.getString("Cost"));
                        BigInteger costPr = new BigInteger(rsPermutedRecords.getString("Cost"));
                        this.LOG.console(costDr.toString());
                        this.LOG.console(costPr.toString());
                        cost += costDr.intValue();
                    }
                    
                }
                rsDrivingRecords.first();
            }
            
        } catch (SQLException ex) {
            LOG.exception(ex);
        }
        
        this.LOG.console("SP calculated: " + cost);
        
        
        /*
        ResultSet rsPermutedRecords = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE DeviceId = N'" + getId + "'");
        ResultSet rsDrivingRecords = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords");
        
        try {
            while (rsPermutedRecords.next()) {
                while (rsDrivingRecords.next()) {
                    LOG.console(Integer.toString(rsPermutedRecords.getInt("Tag")));
                    LOG.console(Integer.toString(rsDrivingRecords.getInt("Tag")));
                    LOG.console(Integer.toString(rsPermutedRecords.getInt("Cost")));
                    LOG.console(Integer.toString(rsDrivingRecords.getInt("Cost")));
                }
            }
        } catch (SQLException ex) {
            LOG.exception(ex);
        }*/

    }
    
    /**
     *
     * @param jo
     */
    
    public void putControlData1(JSONObject jo) {
        LOG.console(jo.toString());
        DB.connect();

        int getId = this.getDeviceId(jo.getString("id"));
        int cost = 0;

        ResultSet rsVehiclePackage = DB.executeQuery("SELECT Tag FROM Tags WHERE DeviceId = N'" + getId + "'");
        ResultSet rsPermutedPackage = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE DeviceId = N'" + getId + "'");
        
        try {
            while(rsPermutedPackage.next()) {
                this.LOG.console(rsPermutedPackage.getString("Tag"));
                for(int x = 1; x <= jo.length() - 2; x++) {
                    this.LOG.console(jo.getBigInteger("dvi" + x).toString());
                    BigInteger commit = this.PS.commit(new BigInteger(rsPermutedPackage.getString("Tag")), jo.getBigInteger("dvi" + x));
                    while(rsVehiclePackage.next()) {
                        BigInteger tag = new BigInteger(rsVehiclePackage.getString("Tag"));
                        this.LOG.console(tag.toString());
                        if(tag.equals(commit)){
                            this.LOG.console(tag + " == " + commit);
                            
                            ///
                            ///
                            /// TODO COST CALCULATION
                            ///
                            ///
                        }
                    }
                    rsVehiclePackage.first();
                }
            }   
        } catch(SQLException ex) {
            this.LOG.exception(ex);
        }
    }
}