/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.ti.hutzf1.vpriv_server.serviceprovider;

import ch.bfh.ti.hutzf1.vpriv_server.crypto.OneWayFunction;
import ch.bfh.ti.hutzf1.vpriv_server.crypto.PedersenScheme;
import ch.bfh.ti.hutzf1.vpriv_server.db.DB;
import ch.bfh.ti.hutzf1.vpriv_server.log.Log;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.json.JSONObject;

/**
 *
 * @author fh
 */

public class ServiceProvider {
    
    private final PedersenScheme ps;
    private final Log log;
    private final OneWayFunction hash;
    private final DB DB;
    /*private final ArrayList<RoundPackage> RP = new ArrayList<>();
    private final ArrayList<DrivingTuple> W = new ArrayList<>();
    private final ArrayList<CostTuple> COSTS = new ArrayList<>();
    private final ArrayList<PermutatedPackage> PP = new ArrayList<>();*/
    
    public ServiceProvider() throws IOException{
        this.log = new Log();
        this.ps = new PedersenScheme();
        this.hash =  new OneWayFunction();
        this.DB = new DB();
    }
    
    public void putRoundPackage(JSONObject jo) throws SQLException {
        DB.connect();
        DB.execute("INSERT INTO Vehicles VALUES (N'" + jo.getString("id") + "');");
        log.console("INSERT INTO Vehicles VALUES (N'" + jo.getString("id") + "');");
        ResultSet rs = DB.executeQuery("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "'");
        String getId = "";
        while (rs.next()) {
            getId = rs.getString("ID");
        }
        log.console(rs.toString());
        log.console("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "';");
        for(int i = 0; i < jo.length()-4; i++) {
            DB.execute("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", " + jo.getBigInteger("v" + i) + ");");
            log.console("INSERT INTO Tags VALUES (" + getId + ", " + jo.getInt("round") + ", " + jo.getBigInteger("v" + i) + ");");
        }
        DB.execute("INSERT INTO RoundKeys VALUES (" + getId + ", " + jo.getInt("round") + ", " + jo.getInt("key") + ");");
        log.console("INSERT INTO RoundKeys VALUES (" + getId + ", " + jo.getInt("round") + ", " + jo.getInt("key") + ");");
        DB.disconnect();
    }
    
    public void putDrivingData(JSONObject jo) throws SQLException {
        Random rand = new Random();
        int toll = rand.nextInt(10);
        DB.connect();
        DB.execute("INSERT INTO DrivingRecords VALUES (" + jo.getInt("tag") + ", " + jo.getInt("longitude") + ", " + jo.getInt("latitude") + ", N'" + jo.getString("timestamp") + "', " + toll + ");");
        log.console("INSERT INTO DrivingRecords VALUES (" + jo.getInt("tag") + ", " + jo.getInt("longitude") + ", " + jo.getInt("latitude") + ", N'" + jo.getString("timestamp") + "', " + toll + ");");
        DB.disconnect();
    }    
    
    public void putCostData(JSONObject jo) throws SQLException {
        DB.connect();
        ResultSet rs = DB.executeQuery("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "'");
        String getId = "";
        while (rs.next()) {
            getId = rs.getString("ID");
        }
        log.console(rs.toString());
        log.console("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "';");
        DB.execute("INSERT INTO Round VALUES (" + getId + ", 0, " + jo.getInt("cost") + ");");
        log.console("INSERT INTO Round VALUES (" + getId + ", 0, " + jo.getInt("cost") + ");");
        DB.disconnect();
    }

     public void putPermutedPackage(JSONObject jo) throws SQLException {
        DB.connect();
        ResultSet rs = DB.executeQuery("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "'");
        String getId = "";
        while (rs.next()) {
            getId = rs.getString("ID");
        }
        log.console(rs.toString());
        log.console("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "';");
        for(int x = 1; x <= (jo.length() - 2) / 2; x++) {
            DB.execute("INSERT INTO PermutedRecords VALUES (" + getId + ", " + jo.getInt("U") + ", " + jo.getInt("w" + x) + ", " + jo.getInt("c" + x) + ");");
            log.console("INSERT INTO PermutedRecords VALUES (" + getId + ", " + jo.getInt("U") + ", " + jo.getInt("w" + x) + ", " + jo.getInt("c" + x) + ");");
        }
        DB.disconnect();
    }

    /*public void putControlData(JSONObject jo) throws SQLException {
        log.console(jo.toString());
    }*/
    
    public JSONObject getControlMethod() {
        JSONObject jo = new JSONObject();
        Random rand = new Random();
        //jo.put("bi", rand.nextInt(2));
        jo.put("bi", 1);
        return jo;
    }
    
    public JSONObject getAllData() throws SQLException {
        JSONObject jo = new JSONObject();
        DB.connect();
        ResultSet rs = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords;");
        int i = 1;
        while (rs.next()) {
            jo.put("w" + i, rs.getInt("Tag"));
            log.console(rs.getString("Tag"));
            jo.put("c" + i, rs.getInt("Cost"));
            log.console(rs.getString("Cost"));
            i++;
        }
        DB.disconnect();
        
        log.console(jo.toString());
        
        return jo;
    }
    
    public void putControlData0(JSONObject jo) throws SQLException {
        log.console(jo.toString());
        DB.connect();
        
        ResultSet rsId = DB.executeQuery("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "'");
        String getId = "";
        while (rsId.next()) {
            getId = rsId.getString("ID");
        }
        
        BigInteger openingKey = jo.getBigInteger("dki");
        
        ResultSet rsKey = DB.executeQuery("SELECT RoundKey FROM RoundKeys WHERE VehicleId = N'" + getId + "'");
        BigInteger getKey = null;

        while (rsKey.next()) {
            getKey = BigInteger.valueOf(rsKey.getInt("RoundKey"));
        }
        
        log.console("Key" + getKey.toString());
        
        
        
        ResultSet rsPermutedRecords = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE VehicleId = N'" + getId + "'");
        ResultSet rsDrivingRecords = DB.executeQuery("SELECT Tag, Cost FROM DrivingRecords");
        while (rsPermutedRecords.next()) {
            while (rsDrivingRecords.next()) {
                log.console(Integer.toString(rsPermutedRecords.getInt("Tag")));
                log.console(Integer.toString(rsDrivingRecords.getInt("Tag")));
                log.console(Integer.toString(rsPermutedRecords.getInt("Cost")));
                log.console(Integer.toString(rsDrivingRecords.getInt("Cost")));
            }
        }
        
        
        
    }
    
    public void putControlData1(JSONObject jo) throws SQLException {
        log.console(jo.toString());
        DB.connect();
        
        ResultSet rsId = DB.executeQuery("SELECT ID FROM Vehicles WHERE LicensePlate = N'" + jo.getString("id") + "'");
        String getId = "";
        while (rsId.next()) {
            getId = rsId.getString("ID");
        }
        
        ResultSet rsVehiclePackage = DB.executeQuery("SELECT Tag FROM Tags WHERE VehicleId = N'" + getId + "'");
        ResultSet rsPermutedPackage = DB.executeQuery("SELECT Tag, Cost FROM PermutedRecords WHERE VehicleId = N'" + getId + "'");
        BigInteger aa;
        BigInteger bb;
        BigInteger cc;
        BigInteger ab;
        while (rsVehiclePackage.next()) {
            while (rsPermutedPackage.next()) {
                for(int x = 1; x <= jo.length() - 2; x++) {
                    aa = BigInteger.valueOf(rsPermutedPackage.getInt("Tag"));
                    bb = jo.getBigInteger("dvi" + x);
                    cc = BigInteger.valueOf(rsVehiclePackage.getInt("Tag"));
                    
                    //log.console(aa.toString());
                    log.console("dvi" + x + " " + bb.toString());
                    log.console("rsVehicleTag " + cc.toString());

                    ab = ps.commit(ps.getElement(cc), ps.getElement(bb)).convertToBigInteger();

                    log.console("commits " + ab.toString());
                    log.console(String.valueOf(cc.equals(ab))); 
                }
       
            }
       
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