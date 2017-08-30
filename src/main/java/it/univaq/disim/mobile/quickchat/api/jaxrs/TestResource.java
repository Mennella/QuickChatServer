/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.disim.mobile.quickchat.api.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mennella
 */
@Path("test")
public class TestResource {
    
    @GET
//    @Path("profile")
    public Response profile() {
        return Response.status(200).build();
    }
    
}
