package com.mycompany.gocommunity;

import db.Client;
import db.Comment;
import db.Project;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Carlos
 */
@Path("/data")
public class ApiBean {
    
    //url structure is: /GoCommunity/api/data/

    private final DatabaseHandler db = new DatabaseHandler("go.odb");
    private final String ha = "Access-Control-Allow-Origin";
    private final String hb = "*";
    private final String hx = "Access-Control-Allow-Methods";
    private final String hy = "POST, GET, OPTIONS, PUT";
 
    @Path("/user/{username}")
    @GET
    @Produces("application/json")
    public Response getUserInfo(@PathParam("username") String username) throws JSONException {
        
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
        JSONArray ownArray = new JSONArray();
        for(Long tmp: owns)
            ownArray.put(tmp);
        
        json.put("owns", owns);
        
        List<Long> follows = c.getFollows();
        JSONArray followArray = new JSONArray();
        for(Long tmp: follows)
            followArray.put(tmp);
        
        json.put("follows", follows);
        
        return Response.status(200).entity(json.toString()).build();
    }
    
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() throws JSONException {
        
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
            item.put("progress", moneyFormatAsDouble(tmp.getProgress()));
            item.put("goal", moneyFormatAsDouble(tmp.getGoal()));
            item.put("endsOn", tmp.getEndsOn());          
            
            List<JSONObject> commentsJson = new ArrayList<>();
            for(Comment c : tmp.getComments()) {
                JSONObject item2 = new JSONObject();
                item2.put("user", c.getUserVisibleName());
                item2.put("date", c.getApiFormattedDate());
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
    public Response getProjectInfo(@PathParam("name") String name) throws JSONException {
        
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
        json.put("goal", moneyFormatAsDouble(p.getGoal()));
        json.put("progress", moneyFormatAsDouble(p.getProgress()));
        json.put("createdOn", p.getCreatedOn());
        json.put("endsOn", p.getEndsOn());

        List<JSONObject> commentsJson = new ArrayList<>();
        for(Comment tmp : p.getComments()) {
            JSONObject item = new JSONObject();
            item.put("user", tmp.getUserVisibleName());
            item.put("date", tmp.getApiFormattedDate());
            item.put("content",tmp.getContent());
            commentsJson.add(item);
        }
        
        json.put("comments",commentsJson);
        

        return Response.status(200).entity(json.toString()).build();
    }
    
    @Path("/search/{query}")
    @GET
    @Produces("application/json")
    public Response searchProject(@PathParam("query") String query) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        
        if (query==null || query.equals(""))
            return Response.status(200).entity(invalid.toString()).build();
              
        List<JSONObject> res = new ArrayList<>();
        
        List<Project> list = db.searchProjects(query);
        for (Project p: list) {
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
            json.put("goal", moneyFormatAsDouble(p.getGoal()));
            json.put("progress", moneyFormatAsDouble(p.getProgress()));
            json.put("createdOn", p.getCreatedOn());
            json.put("endsOn", p.getEndsOn());
            
            List<JSONObject> cmt = new ArrayList<>();
            for(Comment key : p.getComments()){
                JSONObject item = new JSONObject();
                item.put("user", key.getUserVisibleName());
                item.put("date", key.getApiFormattedDate());
                item.put("content", key.getContent());
                cmt.add(item);
            }
        
            json.put("comments", cmt);
            res.add(json);
        }
            
        JSONObject result = new JSONObject();
        result.put("list", res);
        
        return Response.status(200).entity(result.toString()).build();
        
    }
    
    @Path("/createAccount")
    @GET
    @Produces("application/json")
    public Response createAccount(
            @QueryParam("user") String userVisible,
            @QueryParam("username") String username,
            @QueryParam("pword") String pword) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject exists = createErrorMessage("Username already exists.", 404);
        
        if (userVisible==null || username==null || pword==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        Client c = new Client(userVisible, username, pword);
        long id = db.apiCreateAccount(c);
        
        if (id==-1) {
            return Response.status(200).entity(exists.toString()).build();
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return Response.status(200).entity(res.toString()).build();
        }
    
    }
    
    @Path("/createProject")
    @GET
    @Produces("application/json")
    public Response createProject(
            @QueryParam("name") String name,
            @QueryParam("desc") String desc,
            @QueryParam("goal") String goal,
            @QueryParam("date") String date,
            @QueryParam("owner") String owner) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject exists = createErrorMessage("Name already exists.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        JSONObject dateError = createErrorMessage("Invalid date.", 404);
        
        if (name==null || desc==null || goal==null || date==null || owner==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        double goalValue;
        long ownerId;
        Date end;
        
        try {
            goalValue = Double.parseDouble(goal);
            ownerId = Long.parseLong(owner);
            end = Date.valueOf(date);
        } catch (NumberFormatException e) {
            return Response.status(200).entity(invalid.toString()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        if (end.before(new Date(Calendar.getInstance().getTime().getTime()))) {
            return Response.status(200).entity(dateError.toString()).build();
        }
        
        Project newProject = new Project(name, desc, goalValue, end, ownerId);
        long id = db.apiCreateProject(newProject);
        
        if (id==-1) {
            return Response.status(200).entity(exists.toString()).build();
        } else if (id==-2) {
            return Response.status(200).entity(badUser.toString()).build();
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return Response.status(200).entity(res.toString()).build();
        }
        
    }
    
    @Path("/donate")
    @GET
    @Produces("application/json")
    public Response donate(
            @QueryParam("amt") String amt,
            @QueryParam("pid") String pid) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        
        if (amt==null || pid==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        boolean donate = db.apiDonate(amtValue, pidValue);
        
        if (donate) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return Response.status(200).entity(res.toString()).build();
        } else {
            return Response.status(200).entity(badProject.toString()).build();
        }
    }
    
    @Path("/addMilestone")
    @GET
    @Produces("application/json")
    public Response addMilestone(
            @QueryParam("amt") String amt,
            @QueryParam("desc") String desc,
            @QueryParam("pid") String pid) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        
        if (amt==null || desc==null || pid==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        boolean add = db.apiAddMilestone(amtValue, desc, pidValue);
        
        if (add) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return Response.status(200).entity(res.toString()).build();
        } else {
            return Response.status(200).entity(badProject.toString()).build();
        }
    }
    
    @Path("/addComment")
    @GET
    @Produces("application/json")
    public Response addComment(
            @QueryParam("content") String content,
            @QueryParam("uid") String uid,
            @QueryParam("pid") String pid) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        
        if (content==null || uid==null || pid==null) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        long uidValue;
        long pidValue;
        
        try {
            uidValue = Long.parseLong(uid);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return Response.status(200).entity(invalid.toString()).build();
        }
        
        int add = db.apiAddComment(uidValue, content, pidValue);
        
        switch (add) {
            case 0:
                JSONObject res = new JSONObject();
                res.put("status", "success");
                return Response.status(200).entity(res.toString()).build();
            case -1:
                return Response.status(200).entity(badUser.toString()).build();
            default:
                return Response.status(200).entity(badProject.toString()).build();
        }
    }
    
    @Path("/login")
    @GET
    @Produces("application/json")
    public Response getLoginResult(
            @QueryParam("user") String user,
            @QueryParam("pword") String pword) throws JSONException {
        
        Client c = null;
        JSONObject notAllowed = createErrorMessage("Not Allowed.", 404);
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (user==null || pword==null) {
            return Response.status(200).header(ha, hb).header(hx, hy).entity(invalid.toString()).build();
        }
        
        c = db.apiGetUser(user);
        if (c==null) {
            return Response.status(200).header(ha, hb).header(hx, hy).entity(notFound.toString()).build();
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
                return Response.status(200).header(ha, hb).header(hx, hy).entity(json.toString()).build();
            case 1:
                json.put("status", "false");
                return Response.status(200).header(ha, hb).header(hx, hy).entity(json.toString()).build();
            default:
                return Response.status(200).header(ha, hb).header(hx, hy).entity(notAllowed.toString()).build();
        }
    }
    
    private String moneyFormat(double original) {
        return String.format("%.2f", original);
    }
    
    private double moneyFormatAsDouble(double original) {
        return Double.parseDouble(moneyFormat(original));
    }
    
    private String cleanDescription(String original) {
        return original.replace("\t", "\\t")
                       .replace("\r", "\\r")
                       .replace("\n", "\\n");
    }
    
    private JSONObject createErrorMessage(String message, int code) throws JSONException{
        JSONObject error = new JSONObject();
        JSONObject tmp = new JSONObject();
        
        tmp.put("code", code);
        tmp.put("message", message);
        
        error.put("error", tmp);
        
        return error;
    }
}
