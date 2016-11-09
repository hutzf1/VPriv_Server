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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author fh
 */
@Path("ServiceProvider/{Parameter}")
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
     * @param parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@PathParam("Parameter") String parameter) {
        System.out.println(parameter);
        JSONObject jo = new JSONObject();
        switch (parameter) {
            case "getAllData":
                sp.
                return null;
            case "getControlMethod":
                return sp.getControlMethod().toString();
        }
        return null;
    }

    /**
     * PUT method for updating or creating an instance of ServiceProviderResource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(@PathParam("Parameter") String parameter, String str) throws SQLException {
        JSONObject jo = new JSONObject(str);
        System.out.println(parameter);
        switch (parameter) {
            case "putRoundpPackage":
                sp.putRoundPackage(jo);
                break;
            case "putDrivingData":
                sp.putDrivingData(jo);
                break;
            case "putCostData":
                sp.putCostData(jo);
                break;
            case "putPermutedPackage":
                sp.putPermutedPackage(jo);
                break;
            case "putControlData":
                sp.putControlData(jo);
                break;
        }
    }
}
