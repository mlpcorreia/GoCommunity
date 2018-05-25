package com.mycompany.gocommunity;

import db.Client;
import db.Comment;
import db.Project;
import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

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
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (username==null || username.equals(""))
            return Response.status(200).entity(invalid.toString()).build();
        
        try {
            long id = Long.parseLong(username);
            c = db.apiGetUser(id);                
        } catch (NumberFormatException e) {
            c = db.apiGetUser(username);
        }
        
        if (c==null)
            return Response.status(200).entity(notFound.toString()).build();
        
        JSONObject json = new JSONObject();
        
        json.put("id", c.getId());
        json.put("username", c.getUsername());
        json.put("name", c.getName());
        
        List<Long> owns = c.getOwns();
        json.put("owns", new JSONArray());
        for(Long tmp: owns)
            json.accumulate("owns", tmp);
        
        List<Long> follows = c.getFollows();
        json.put("follows", new JSONArray());
        for(Long tmp: follows)
            json.accumulate("follows", tmp);
        
        return Response.status(200).entity(json.toString()).build();
    }
    
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() {
        
        List<Project> top = db.getPopularProjects();
        JSONObject error = createErrorMessage("No projects found!",404);
        
        if(top.isEmpty())
            return Response.status(200).entity(error.toString()).build();
            
        JSONObject json = new JSONObject();
        List<JSONObject> projects = new ArrayList<>();
        for(Project tmp : top){
            JSONObject item = new JSONObject();
            item.put("id", tmp.getId());
            item.put("name", tmp.getName());
            item.put("progress", moneyFormat(tmp.getProgress()));
            item.put("goal", moneyFormat(tmp.getGoal()));
            json.put("progress", moneyFormat(tmp.getProgress()));
            item.put("endsOn", tmp.getEndsOn());
            
            
            
            List<Comment> comments = tmp.getComments();
            List<JSONObject> commentsJson = new ArrayList<>();
            for(Comment c : comments) {
                JSONObject item2 = new JSONObject();
                item2.put("user", c.getUsername());
                item2.put("date", c.getDate());
                item2.put("content",c.getContent());
                commentsJson.add(item2);
            }
            
            item.put("comments", commentsJson);
            
            projects.add(item);
        }
        
        json.put("list", projects);
        
        return Response.status(200).entity(json.toString()).build();
    }
    
    @Path("/project/{name}")
    @GET
    @Produces("application/json")
    public Response getProjectInfo(@PathParam("name") String name) {
        
        Project p = null;
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (name==null || name.equals(""))
            return Response.status(200).entity(invalid.toString()).build();
        
        try {
            long id = Long.parseLong(name);
            p = db.apiGetProject(id);                
        } catch (NumberFormatException e) {
            p = db.apiGetProject(name);
        }
        
        if (p==null)
            return Response.status(200).entity(notFound.toString()).build();
        
        JSONObject json = new JSONObject();
        
        json.put("id", p.getId());
        json.put("name", p.getName());
        json.put("description", cleanDescription(p.getDescription()));
        
        List<Double> milestoneKeys = p.getMilestoneKeys();
        List<JSONObject> milestones = new ArrayList<>();
        for(Double key : milestoneKeys){
            JSONObject item = new JSONObject();
            item.put(moneyFormat(key), p.getMilestoneText(key));
            milestones.add(item);
        }
        
        json.put("milestones", milestones);
        json.put("goal", moneyFormat(p.getGoal()));
        json.put("progress", moneyFormat(p.getProgress()));
        json.put("createdOn", p.getCreatedOn());
        json.put("endsOn", p.getEndsOn());
        
        List<Long> followers = p.getFollowers();
        for(Long key : followers)
            json.accumulate("followers", key);
        
        List<Comment> comments = p.getComments();
        List<JSONObject> commentsJson = new ArrayList<>();
        for(Comment tmp : comments) {
            JSONObject item = new JSONObject();
            item.put("user", tmp.getUsername());
            item.put("date", tmp.getDate());
            item.put("content",tmp.getContent());
            commentsJson.add(item);
        }
        
        json.put("comments",commentsJson);
        

        return Response.status(200).entity(json.toString()).build();
    }
    
    @Path("/login")
    @GET
    @Produces("application/json")
    public Response getLoginResult(
            @QueryParam("user") String user,
            @QueryParam("pword") String pword) {
        
        Client c = null;
        JSONObject notAllowed = createErrorMessage("Not Allowed.", 404);
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (user==null || pword==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        c = db.apiGetUser(user);
        if (c==null) {
            return Response.status(200).entity(notFound.toString()).build();
        }
        
        int res = db.tryLogin(c, pword);
        JSONObject json = new JSONObject();
        //0 = can login, valid
        //1 = can login, failed
        //2 = cant login
        switch (res) {                       
            case 0:             
                json.put("status", "true");
                json.put("id", c.getId());
                return Response.status(200).entity(json.toString()).build();
            case 1:
                json.put("status", "false");
                return Response.status(200).entity(json.toString()).build();
            default:
                return Response.status(200).entity(notAllowed.toString()).build();
        }
    }
    
    private String moneyFormat(double original) {
        return String.format("%.2f", original);
    }
    
    private String cleanDescription(String original) {
        return original.replace("\t", "\\t")
                       .replace("\r", "\\r")
                       .replace("\n", "\\n");
    }
    
    private JSONObject createErrorMessage(String message, int code){
        JSONObject error = new JSONObject();
        JSONObject tmp = new JSONObject();
        
        tmp.put("code", code);
        tmp.put("message", message);
        
        error.put("error", tmp);
        
        return error;
    }
}
