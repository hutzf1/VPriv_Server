/*
 * VPriv Client Server Simulator
 * Copyright 2017 Fabian Hutzli
 * Berner Fachhochschule
 *
 * All rights reserved.
 */
package ch.bfh.ti.hutzf1.vpriv_server;

import ch.bfh.ti.hutzf1.vpriv_server.serviceprovider.ServiceProvider;
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
 * @author Fabian Hutzli
 */
@Path("ServiceProvider/{Parameter}")
public class ServiceProviderResource {
    @Context
    private UriInfo context;

    private final ServiceProvider sp;
    
    /**
     * Creates a new instance of ServiceProviderResource
     */
    
    public ServiceProviderResource() {
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
                return sp.getAllData().toString();
            case "getControlMethod":
                return sp.getControlMethod().toString();
        }
        return null;
    }

    /**
     * PUT method for updating or creating an instance of ServiceProviderResource
     * @param parameter
     * @param str
     */
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(@PathParam("Parameter") String parameter, String str) {
        JSONObject jo = new JSONObject(str);
        System.out.println(parameter);
        switch (parameter) {
            case "putRoundPackage":
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
            case "putControlData0":
                sp.putControlData0(jo);
                break;
            case "putControlData1":
                sp.putControlData1(jo);
                break;
        }
    }
}
