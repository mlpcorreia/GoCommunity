package com.mycompany.gocommunity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Carlos
 */
@Path("/data")
public class ApiBean {
    
    private final ComBean bean = new ComBean();
 
    @Path("/user/{id}")
    @GET
    @Produces("application/json")
    public Response convert(@PathParam("id") String from) {
        
        String error = "{\"error\":\"invalid parameters\"}";
        
        return Response.status(200).entity("{\"hi\":\"hi\"}").build();
    }
}
