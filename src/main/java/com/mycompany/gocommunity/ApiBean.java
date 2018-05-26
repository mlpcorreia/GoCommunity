package com.mycompany.gocommunity;

import db.Client;
import db.Comment;
import db.Project;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ws.rs.Consumes;
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
 
    @Path("/user/{username}")
    @GET
    @Produces("application/json")
    public Response getUserInfo(@PathParam("username") String username) throws JSONException {
        
        Client c = null;
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (username==null || username.equals(""))
            return getResponse(invalid);
        
        try {
            long id = Long.parseLong(username);
            c = db.apiGetUser(id);                
        } catch (NumberFormatException e) {
            c = db.apiGetUser(username);
        }
        
        if (c==null)
            return getResponse(notFound);
        
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
        
        return getResponse(json);
    }
    
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() throws JSONException {
        
        List<Project> top = db.getPopularProjects();
        JSONObject error = createErrorMessage("No projects found!",404);
        
        if(top.isEmpty())
            return getResponse(error);
            
        JSONObject json = new JSONObject();
        List<JSONObject> projects = new ArrayList<>();
        for(Project tmp : top){
            JSONObject item = new JSONObject();
            item.put("id", tmp.getId());
            item.put("name", tmp.getName());
            item.put("owner", tmp.getOwner());
            item.put("description", cleanDescription(tmp.getDescription()));
            
            List<Double> milestoneKeys = tmp.getMilestoneKeys();
            List<JSONObject> milestones = new ArrayList<>();
            for(Double key : milestoneKeys){
                JSONObject mitem = new JSONObject();
                mitem.put(moneyFormat(key), tmp.getMilestoneText(key));
                milestones.add(mitem);
        }
        
            item.put("milestones", milestones);
            item.put("progress", moneyFormatAsDouble(tmp.getProgress()));
            item.put("goal", moneyFormatAsDouble(tmp.getGoal()));
            item.put("createdOn", tmp.getCreatedOn());
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
        
        return getResponse(json);
    }
    
    @Path("/project/{name}")
    @GET
    @Produces("application/json")
    public Response getProjectInfo(@PathParam("name") String name) throws JSONException {
        
        Project p = null;
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        if (name==null || name.equals(""))
            return getResponse(invalid);
        
        try {
            long id = Long.parseLong(name);
            p = db.apiGetProject(id);                
        } catch (NumberFormatException e) {
            p = db.apiGetProject(name);
        }
        
        if (p==null)
            return getResponse(notFound);
        
        JSONObject json = new JSONObject();
        
        json.put("id", p.getId());
        json.put("name", p.getName());
        json.put("owner", p.getOwner());
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
        

        return getResponse(json);
    }
    
    @Path("/search/{query}")
    @GET
    @Produces("application/json")
    public Response searchProject(@PathParam("query") String query) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameter!", 404);
        
        if (query==null || query.equals(""))
            return getResponse(invalid);
              
        List<JSONObject> res = new ArrayList<>();
        
        List<Project> list = db.searchProjects(query);
        for (Project p: list) {
            JSONObject json = new JSONObject();
            
            json.put("id", p.getId());
            json.put("name", p.getName());
            json.put("owner", p.getOwner());
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
        
        return getResponse(result);
        
    }
    
    @Path("/follow")
    @GET
    @Produces("application/json")
    public Response changeStance(
            @QueryParam("uid") String uid,
            @QueryParam("pid") String pid) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        JSONObject isOwner = createErrorMessage("User is project owner.", 404);
        
        if (uid==null || pid==null || uid.equals("") || pid.equals("")) {
            return getResponse(invalid);
        }
        
        long uidValue;
        long pidValue;
        
        try {
            uidValue = Long.parseLong(uid);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return getResponse(invalid);
        }
        
        //-3 = is owner
        //-2 = bad project
        //-1 = bad user
        //0 = unfollow
        //1 = follow
        int res = db.apiChangeStance(uidValue, pidValue);
        JSONObject json = new JSONObject();
        
        switch (res) {
            case -3:
                return getResponse(isOwner);
            case -2:
                return getResponse(badProject);
            case -1:
                return getResponse(badUser);
            case 0:              
                json.put("status", "unfollowed");
                return getResponse(json);
            default:
                json.put("status", "followed");
                return getResponse(json);
        }
        
        
        
    }
    
    @Path("/createAccount")
    @POST
    @Consumes("application/json")
    public Response createAccount(
            @QueryParam("user") String userVisible,
            @QueryParam("username") String username,
            @QueryParam("pword") String pword) throws JSONException {
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject exists = createErrorMessage("Username already exists.", 404);
        
        if (userVisible==null || username==null || pword==null) {
            return getResponse(invalid);
        }
        
        Client c = new Client(userVisible, username, pword);
        long id = db.apiCreateAccount(c);
        
        if (id==-1) {
            return getResponse(exists);
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return getResponse(res);
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
        
        if (name==null || desc==null || goal==null || date==null || owner==null ||
                name.equals("") || desc.equals("") || goal.equals("") ||
                date.equals("") || owner.equals("")) {
            return getResponse(invalid);
        }
        
        double goalValue;
        long ownerId;
        Date end;
        
        try {
            goalValue = Double.parseDouble(goal);
            ownerId = Long.parseLong(owner);
            end = Date.valueOf(date);
        } catch (NumberFormatException e) {
            return getResponse(invalid);
        } catch (IllegalArgumentException e) {
            return getResponse(invalid);
        }
        
        if (end.before(new Date(Calendar.getInstance().getTime().getTime()))) {
            return getResponse(dateError);
        }
        
        Project newProject = new Project(name, desc, goalValue, end, ownerId);
        long id = db.apiCreateProject(newProject);
        
        if (id==-1) {
            return getResponse(exists);
        } else if (id==-2) {
            return getResponse(badUser);
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return getResponse(res);
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
        
        if (amt==null || pid==null || amt.equals("") || pid.equals("")) {
            return getResponse(invalid);
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return getResponse(invalid);
        }
        
        boolean donate = db.apiDonate(amtValue, pidValue);
        
        if (donate) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return getResponse(res);
        } else {
            return getResponse(badProject);
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
        
        if (amt==null || desc==null || pid==null || amt.equals("") ||
                desc.equals("") || pid.equals("")) {
            return getResponse(invalid);
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return getResponse(invalid);
        }
        
        boolean add = db.apiAddMilestone(amtValue, desc, pidValue);
        
        if (add) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return getResponse(res);
        } else {
            return getResponse(badProject);
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
        
        if (content==null || uid==null || pid==null || content.equals("") ||
                uid.equals("") || pid.equals("")) {
            return getResponse(invalid);
        }
        
        long uidValue;
        long pidValue;
        
        try {
            uidValue = Long.parseLong(uid);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return getResponse(invalid);
        }
        
        int add = db.apiAddComment(uidValue, content, pidValue);
        
        switch (add) {
            case 0:
                JSONObject res = new JSONObject();
                res.put("status", "success");
                return getResponse(res);
            case -1:
                return getResponse(badUser);
            default:
                return getResponse(badProject);
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
        
        if (user==null || pword==null || user.equals("") || pword.equals("")) {
            return getResponse(invalid);
        }
        
        c = db.apiGetUser(user);
        if (c==null) {
            return getResponse(notFound);
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
                return getResponse(json);
            case 1:
                json.put("status", "false");
                return getResponse(json);
            default:
                return getResponse(notAllowed);
        }
    }
    
    private Response getResponse(JSONObject json) {
        String ha = "Access-Control-Allow-Origin";
        String hb = "*";
        String hx = "Access-Control-Allow-Methods";
        String hy = "POST, GET, OPTIONS, PUT"; 
        return Response.status(200).header(ha, hb).header(hx, hy).entity(json.toString()).build();
    }
    
    /*private Response postResponse(JSONObject json) {
        String ha = "Access-Control-Allow-Origin";
        String hb = "*";
        String hx = "Access-Control-Allow-Methods";
        String hy = "POST, GET, OPTIONS, PUT"; 
        return Response.status(201).header(ha, hb).header(hx, hy).entity(json.toString()).build();
    }*/
    
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
