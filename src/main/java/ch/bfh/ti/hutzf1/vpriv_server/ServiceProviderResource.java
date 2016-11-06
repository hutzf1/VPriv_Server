/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.ti.hutzf1.vpriv_server;

import ch.bfh.ti.hutzf1.vpriv_server.serviceprovider.ServiceProvider;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author fh
 */
@Path("ServiceProvider")
public class ServiceProviderResource {

    @Context
    private UriInfo context;

    private final ServiceProvider sp;
    /**
     * Creates a new instance of ServiceProviderResource
     * @throws java.io.IOException
     */
    public ServiceProviderResource() throws IOException {
        this.sp = new ServiceProvider();
    }

    /**
     * Retrieves representation of an instance of ch.bfh.ti.hutzf1.vpriv_server.ServiceProviderResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        return "get completed";
        
    }

    /**
     * PUT method for updating or creating an instance of ServiceProviderResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String str) throws SQLException {
        JSONObject jo = new JSONObject(str);
        System.out.println(jo.getString("type"));
        switch (jo.getString("type")) {
            case "roundpackage":
                //System.out.println("--roundpackage");
                sp.putVehicleData(jo);
                break;
            case "drivingdata":
                //System.out.println("--drivingdata");
                sp.putDrivingData(jo);
                break;
            case "costdata":
                //System.out.println("--costdata");
                sp.putCostData(jo);
                break;
        }
    }
}
