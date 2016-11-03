/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.ti.hutzf1.vpriv_server;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author fh
 */
@Path("ServiceProvider")
public class ServiceProviderResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ServiceProviderResource
     */
    public ServiceProviderResource() {
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
    public void putJson(String content) {
        System.out.println(content);
    }
}
