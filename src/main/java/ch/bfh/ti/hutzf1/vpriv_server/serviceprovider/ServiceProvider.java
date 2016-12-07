package ch.bfh.ti.hutzf1.vpriv_server.serviceprovider;

import ch.bfh.ti.hutzf1.vpriv_server.crypto.OneWayFunction;
import ch.bfh.ti.hutzf1.vpriv_server.crypto.PedersenScheme;
import ch.bfh.ti.hutzf1.vpriv_server.db.DB;
import ch.bfh.ti.hutzf1.vpriv_server.log.Log;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Fabian Hutzli
 */

public class ServiceProvider {
    
    private final PedersenScheme ps;
    private final Log log;
    private final OneWayFunction hash;
    private final DB DB;

    /**
     *
     */
    
    public ServiceProvider() {
        this.log = new Log();
        this.ps = new PedersenScheme();
        this.hash =  new OneWayFunction();
        this.DB = new DB();
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
            log.console("SELECT ID FROM Devices WHERE DeviceIdentity = N'" + DeviceIdentity + "';");
            
            ResultSet rs = DB.executeQuery("SELECT ID FROM Devices WHERE DeviceIdentity = N'" + DeviceIdentity + "'");
            
            while (rs.next()) {
                deviceId = rs.getInt("ID");    
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
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
            //cleanUp();
            log.console("INSERT INTO Devices VALUES (N'" + jo.getString("id") + "');");
            DB.execute("INSERT INTO Devices VALUES (N'" + jo.getString("id") + "');");
        }
        
        int getId = this.getDeviceId(jo.getString("id"));
        
        for(int i = 0; i < jo.length()-4; i++) {
            log.console("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("v" + i) + "');");
            DB.execute("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("v" + i) + "');");
        }
        
        log.console("INSERT INTO RoundKeys VALUES (" + getId + ", " + jo.getInt("round") + ", N'" + jo.getBigInteger("key") + "');");
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

        log.console("INSERT INTO DrivingRecords VALUES (N'" + jo.getBigInteger("tag") + "', " + jo.getInt("longitude") + ", " + jo.getInt("latitude") + ", N'" + jo.getString("timestamp") + "', " + toll + ");");
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
        
        log.console("INSERT INTO Round VALUES (" + getId + ", 0, " + jo.getInt("cost") + ");");
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
            log.console("INSERT INTO PermutedRecords VALUES (" + getId + ", " + jo.getInt("U") + ", N'" + jo.getBigInteger("w" + x) + "', N'" + jo.getBigInteger("c" + x) + "');");
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
        jo.put("bi", 1);
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
                log.console(rs.getString("Tag"));
                jo.put("c" + i, rs.getInt("Cost"));
                log.console(rs.getString("Cost"));
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DB.disconnect();
        
        return jo;
    }
    
    /**
     *
     * @param jo
     */
    
    public void putControlData0(JSONObject jo) {
        log.console(jo.toString());
        DB.connect();

        int getId = this.getDeviceId(jo.getString("id"));

        BigInteger openingKey = jo.getBigInteger("dki");
        BigInteger getKey = null;   
        
        ResultSet rsKey = DB.executeQuery("SELECT RoundKey FROM RoundKeys WHERE VehicleId = N'" + getId + "'");
        
        try {
            while (rsKey.next()) {
                getKey = BigInteger.valueOf(rsKey.getLong("RoundKey"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        ResultSet rsPermutedRecords = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE VehicleId = N'" + getId + "'");
        ResultSet rsDrivingRecords = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords");
        
        try {
            while (rsPermutedRecords.next()) {
                while (rsDrivingRecords.next()) {
                    log.console(Integer.toString(rsPermutedRecords.getInt("Tag")));
                    log.console(Integer.toString(rsDrivingRecords.getInt("Tag")));
                    log.console(Integer.toString(rsPermutedRecords.getInt("Cost")));
                    log.console(Integer.toString(rsDrivingRecords.getInt("Cost")));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     *
     * @param jo
     */
    
    public void putControlData1(JSONObject jo) {
        log.console(jo.toString());
        DB.connect();

        BigInteger aa;
        BigInteger bb;
        BigInteger cc;
        BigInteger ab;

        int getId = this.getDeviceId(jo.getString("id"));

        ResultSet rsVehiclePackage = DB.executeQuery("SELECT Tag FROM Tags WHERE VehicleId = N'" + getId + "'");
        ResultSet rsPermutedPackage = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE VehicleId = N'" + getId + "'");
            
        try {
            while (rsVehiclePackage.next()) {
                while (rsPermutedPackage.next()) {
                    for(int x = 1; x <= jo.length() - 2; x++) {
                        aa = BigInteger.valueOf(rsPermutedPackage.getInt("Tag"));
                        bb = jo.getBigInteger("dvi" + x);
                        cc = BigInteger.valueOf(rsVehiclePackage.getInt("Tag"));
                        
                        //log.console(aa.toString());
                        log.console("dvi" + x + " " + bb.toString());
                        log.console("rsVehicleTag " + cc.toString());
                        
                        ab = ps.commit(cc, bb);
                        
                        log.console("commits " + ab.toString());
                        log.console(String.valueOf(cc.equals(ab)));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
            
            // SP has: License Plate (id), round (i), commitment of hased tags, all Tags of Driving Phase (unhashed, uncommited)
            // SP recieves: License Plate (id), Opening Keys for Tags (DV), Opening key for costs (Di), All tags of Driving Phase (permuted and commitment of)
            // for bi = 1, SP needs:
            /*
            
            int sum = 0;
            
            for (DrivingTuple d1 : thispp.getDrivingTuples()) {
            int i = 0;
            
            for (Element e : thisrp.getCommits()) {
            if(e.equals(ps.commit(d1.tag, dv.get(i)))) {
            log.file(d1.cost.toString());
            for(DrivingTuple d2 : W) {
            if(e.equals(ps.commit(d2.tag, dv.get(i)))){
            sum = sum + d2.cost.convertToBigInteger().intValue();
            break;
            }
            }
            }
            }
            i++;
            }*/
    
    }
    
    /*public int calculate0(String id, Element key, ArrayList<Element> dc) throws IOException {
        log.file("-----------------------------------");
        log.file("BI = 0, Service Provider calculates");
        log.file("-----------------------------------");
        
        PermutatedPackage vehiclesPackage = null;
        
        for (PermutatedPackage pp : PP) {
            if(pp.getId().equals(id)) {
                vehiclesPackage = pp;
                log.console("Package: " + vehiclesPackage.getId());
            }
        }*/
        
        /*RoundPackage vehiclesRound = null;
        
        for (RoundPackage rp : RP) {
            if(rp.id == id) {
                vehiclesRound = rp;
                log.console("Package: " + vehiclesPackage.id);
            }
        }*/
         
        /*int sum = 0;
        
        for (DrivingTuple dt : W) {
            int m = 0;
            for (DrivingTuple vehicleDt : vehiclesPackage.getDrivingTuples()) {
                if(ps.commit(dt.cost, dc.get(m)).equals(vehicleDt.cost)) {
                if(hash.getHash(dt.tag, key).equals(vehicleDt.tag)) {
                    //if(ps.commit(dt.cost, dc.get(m)).equals(vehicleDt.cost)) {
                        log.file(ps.commit(dt.cost, dc.get(m)).toString());
                        log.file(vehicleDt.cost.toString());
                        sum = sum + dt.cost.convertToBigInteger().intValue();
                        break;
                    }
                }
                m++;
            }
        }
        
        // Ich erhalte
        // id vom Fahrzeug
        // dki vom Fahrzeug
        // dci1 - dcim vom Fahrzeug
        //
        // Ich habe
        // Ui mit fki(w), c(dci)
        
        log.file("-----------------------------------");
        log.file("-----------------------------------");
        return sum;
    }
    
    public int calculate1(String id, ArrayList<Element> dv, Element Di) throws IOException {
        log.file(id + " calculating by service provider");
        log.file("-----------------------------------");
        log.file("-----------------------------------");
        
        // SP has: License Plate (id), round (i), commitment of hased tags, all Tags of Driving Phase (unhashed, uncommited)
        // SP recieves: License Plate (id), Opening Keys for Tags (DV), Opening key for costs (Di), All tags of Driving Phase (permuted and commitment of)
        // for bi = 1, SP needs:
        
        PermutatedPackage thispp = null;
        RoundPackage thisrp = null;
        
        for (PermutatedPackage pp : PP){
            if(pp.getId().equals(id)) {
                thispp = pp;
                log.file("pp: " + thispp.getId());
            }
        }
        
        for (RoundPackage rp : RP){
            if(rp.getId().equals(id)) {
                thisrp = rp;
                log.file("rp: " + thisrp.getId());
            }
        }
        
        int sum = 0;
        
        for (DrivingTuple d1 : thispp.getDrivingTuples()) {
            int i = 0;
            
            for (Element e : thisrp.getCommits()) {
                if(e.equals(ps.commit(d1.tag, dv.get(i)))) {
                    log.file(d1.cost.toString());
                    for(DrivingTuple d2 : W) {
                        if(e.equals(ps.commit(d2.tag, dv.get(i)))){
                            sum = sum + d2.cost.convertToBigInteger().intValue();
                            break;
                        }
                    }
                }
            }
            i++;
        }

        
        
        log.file("-----------------------------------");
        log.file("-----------------------------------");
        
        return sum;
    }*/
}