package com.mycompany.gocommunity;

import db.Client;
import db.Project;
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
    private static final String IDFIELD = "\"id\":";
 
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
        
        StringBuilder res = new StringBuilder();
        
        res.append("{");
        res.append(IDFIELD).append(c.getId()).append(",\"username\":\"").append(c.getUsername()).append("\",");
        res.append("\"name\":\"").append(c.getName()).append("\",\"owns\":[");
        for (int i=0;i<c.getOwns().size();i++) {
            res.append(c.getOwns().get(i));
            
            if (i!=c.getOwns().size()-1) res.append(",");
        }
        res.append("],\"follows\":[");
        for (int i=0;i<c.getFollows().size();i++) {
            res.append(c.getFollows().get(i));
            
            if (i!=c.getFollows().size()-1) res.append(",");
        }
        res.append("]}");
        
        return Response.status(200).entity(res.toString()).build();
    }
    
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() {
        StringBuilder res = new StringBuilder();
        
        res.append("{\"list\":[");
        List<Project> top = db.getPopularProjects();
        for (int i=0;i<top.size();i++) {
            res.append("{");
            res.append(IDFIELD).append(top.get(i).getId()).append(",\"name\":\"").append(top.get(i).getName()).append("\",");
            res.append("\"progress\":").append(moneyFormat(top.get(i).getProgress())).append(",");
            res.append("\"goal\":").append(moneyFormat(top.get(i).getGoal())).append(",");
            res.append("\"endsOn\":\"").append(top.get(i).getEndsOn()).append("\"");
            res.append("}");
            
            if (i!=top.size()-1) res.append(",");
        }
        res.append("]}");
        
        return Response.status(200).entity(res.toString()).build();
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
        
        StringBuilder res = new StringBuilder();
        
        res.append("{");
        res.append(IDFIELD).append(p.getId()).append(",\"name\":\"").append(p.getName()).append("\",");
        res.append("\"owner\":").append(p.getOwner()).append(",");
        res.append("\"description\":\"").append(cleanDescription(p.getDescription())).append("\",");
        res.append("\"milestones\":[");
        for (int i=0;i<p.getAmountOfMilestones();i++) {
            double key = milestoneKeys.get(i);        
            res.append("{").append(moneyFormat(key)).append(":\"").append(p.getMilestoneText(key)).append("\"}");
            
            if (i!=p.getAmountOfMilestones()-1) res.append(",");
        }
        res.append("],\"goal\":").append(moneyFormat(p.getGoal()));
        res.append(",\"progress\":").append(moneyFormat(p.getProgress())).append(",");
        res.append("\"createdOn\":\"").append(p.getCreatedOn()).append("\",");
        res.append("\"endsOn\":\"").append(p.getEndsOn()).append("\",\"followers\":[");
        for (int i=0;i<p.getFollowers().size();i++) {
            res.append(p.getFollowers().get(i));
            
            if (i!=p.getFollowers().size()-1) res.append(",");
        }
        res.append("]}");
        
        return Response.status(200).entity(res.toString()).build();
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
