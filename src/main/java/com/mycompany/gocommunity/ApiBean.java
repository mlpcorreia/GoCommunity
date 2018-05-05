package com.mycompany.gocommunity;

import db.Client;
import db.Project;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author Carlos
 */
@Path("/data")
public class ApiBean {
    
    //url structure is: /GoCommunity/api/data/

    private final DatabaseHandler db = new DatabaseHandler("go.odb");
 
    @Path("/user/{username}")
    @GET
    @Produces("application/json")
    public Response getUserInfo(@PathParam("username") String username) {
        
        Client c = null;
        String invalid = "{\"error\":\"invalid parameter\"}";
        String notFound = "{\"error\":\"not found\"}";
        
        if (username==null || username.equals("")) {
            return Response.status(404).entity(invalid).build();
        }
        
        try {
            long id = Long.parseLong(username);
            c = db.apiGetUser(id);                
        } catch (NumberFormatException e) {
            c = db.apiGetUser(username);
        }
        
        if (c==null) {
            return Response.status(404).entity(notFound).build();
        }
        
        String res = "{";
        res += "\"id\":"+c.getId()+",\"username\":\""+c.getUsername()+"\",";
        res += "\"name\":\""+c.getName()+"\",\"owns\":[";
        for (int i=0;i<c.getOwns().size();i++) {
            res += c.getOwns().get(i);
            
            if (i!=c.getOwns().size()-1) res += ",";
        }
        res += "],\"follows\":[";
        for (int i=0;i<c.getFollows().size();i++) {
            res += c.getFollows().get(i);
            
            if (i!=c.getFollows().size()-1) res += ",";
        }
        res += "]}";
        
        return Response.status(200).entity(res).build();
    }
    
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() {
        String res = "{\"list\":[";
        List<Project> top = db.getPopularProjects();
        for (int i=0;i<top.size();i++) {
            res += "{";
            res += "\"id\":"+top.get(i).getId()+",\"name\":\""+top.get(i).getName()+"\",";
            res += "\"progress\":"+moneyFormat(top.get(i).getProgress())+",";
            res += "\"goal\":"+moneyFormat(top.get(i).getGoal())+",";
            res += "\"endsOn\":\""+top.get(i).getEndsOn()+"\"";
            res += "}";
            
            if (i!=top.size()-1) res += ",";
        }
        res += "]}";
        
        return Response.status(200).entity(res).build();
    }
    
    @Path("/project/{name}")
    @GET
    @Produces("application/json")
    public Response getProjectInfo(@PathParam("name") String name) {
        
        Project p = null;
        String invalid = "{\"error\":\"invalid parameter\"}";
        String notFound = "{\"error\":\"not found\"}";
        
        if (name==null || name.equals("")) {
            return Response.status(404).entity(invalid).build();
        }
        
        try {
            long id = Long.parseLong(name);
            p = db.apiGetProject(id);                
        } catch (NumberFormatException e) {
            p = db.apiGetProject(name);
        }
        
        if (p==null) {
            return Response.status(404).entity(notFound).build();
        }
        
        List<Double> milestoneKeys = p.getMilestoneKeys();
        
        String res = "{";
        res += "\"id\":"+p.getId()+",\"name\":\""+p.getName()+"\",";
        res += "\"owner\":"+p.getOwner()+",";
        res += "\"description\":\""+cleanDescription(p.getDescription())+"\",";
        res += "\"milestones\":[";
        for (int i=0;i<p.getAmountOfMilestones();i++) {
            double key = milestoneKeys.get(i);        
            res += "{"+moneyFormat(key)+":\""+p.getMilestoneText(key)+"\"}";
            
            if (i!=p.getAmountOfMilestones()-1) res += ",";
        }
        res += "],\"goal\":"+moneyFormat(p.getGoal());
        res += ",\"progress\":"+moneyFormat(p.getProgress())+",";
        res += "\"createdOn\":\""+p.getCreatedOn()+"\",";
        res += "\"endsOn\":\""+p.getEndsOn()+"\",\"followers\":[";
        for (int i=0;i<p.getFollowers().size();i++) {
            res += p.getFollowers().get(i);
            
            if (i!=p.getFollowers().size()-1) res += ",";
        }
        res += "]}";
        
        return Response.status(200).entity(res).build();
    }
    
    private String moneyFormat(double original) {
        return String.format("%.2f", original);
    }
    
    private String cleanDescription(String original) {
        return original.replace("\t", "\\t")
                       .replace("\r", "\\r")
                       .replace("\n", "\\n");
    }
}
