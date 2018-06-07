package com.mycompany.gocommunity;

import db.Client;
import db.Comment;
import db.Project;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
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
 * Exposes web resources using REST
 */
@Path("/data")
public class ApiBean {   
    //url structure is: /GoCommunity/api/data/
    private final DatabaseHandler db = new DatabaseHandler("go.odb");   
 
    /**
     * Get the information of a particular user 
     * @param username
     * @return JSON message with the user information
     * @throws JSONException 
     */
    @Path("/user/{username}")
    @GET
    @Produces("application/json")
    public Response getUserInfo(@PathParam("username") String username) throws JSONException {      
        Client c = null;
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        try {
            long id = Long.parseLong(username);
            c = db.apiGetUser(id);                
        } catch (NumberFormatException e) {
            c = db.apiGetUser(username);
        }
        
        if (c==null){
            return getResponse(notFound);
        }
        
        JSONObject json = new JSONObject();
        
        json.put("id", c.getId());
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
    
    /**
     * Get the popular projects
     * @return JSON message with a list that includes the information of the projects
     * @throws JSONException 
     */
    @Path("/popular")
    @GET
    @Produces("application/json")
    public Response getPopularProjects() throws JSONException {       
        List<Project> top = db.getPopularProjects();
            
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
    
    /**
     * Get the information about a project
     * @param name
     * @return JSON message with the project information
     * @throws JSONException 
     */
    @Path("/project/{name}")
    @GET
    @Produces("application/json")
    public Response getProjectInfo(@PathParam("name") String name) throws JSONException {     
        Project p = null;
        JSONObject notFound = createErrorMessage("Not Found!", 404);
        
        try {
            long id = Long.parseLong(name);
            p = db.apiGetProject(id);                
        } catch (NumberFormatException e) {
            p = db.apiGetProject(name);
        }
        
        if (p==null) {
            return getResponse(notFound);
        }
        
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
    
    /**
     * Search for a particular project
     * @param query
     * @return JSON message that contains all the information about a search
     * @throws JSONException 
     */
    @Path("/search/{query}")
    @GET
    @Produces("application/json")
    public Response searchProject(@PathParam("query") String query) throws JSONException {           
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
    
    /**
     * Get information about the follow resource
     * @param body
     * @return JSON message with the available options
     */
    @Path("/follow")
    @OPTIONS
    @Produces("application/json")
    public Response optF(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to follow a project
     * @param body
     * @return JSON message with the response about the project
     * @throws JSONException 
     */
    @Path("/follow")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response changeStance(JsonObject body) throws JSONException {  
        String uid;
        String pid;
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        JSONObject isOwner = createErrorMessage("User is project owner.", 404);
        
        try { 
            uid = body.getString("user");
            pid = body.getString("project");
        } catch (Exception e) {
            return postResponse(invalid);   
        }
        
        if (uid==null || pid==null || uid.equals("") || pid.equals("")) {
            return postResponse(invalid);
        }
        
        long uidValue;
        long pidValue;
        
        try {
            uidValue = Long.parseLong(uid);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return postResponse(invalid);
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
                return postResponse(isOwner);
            case -2:
                return postResponse(badProject);
            case -1:
                return postResponse(badUser);
            case 0:              
                json.put("status", "unfollowed");
                return postResponse(json);
            default:
                json.put("status", "followed");
                return postResponse(json);
        } 
    }
    
    /**
     * Get information about the createAccount resource
     * @param body
     * @return JSON message with the available options
     */   
    @Path("/createAccount")
    @OPTIONS
    @Produces("application/json")
    public Response optCA(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to create an account
     * @param body
     * @return JSON message with the create account result(success, fail,...)
     * @throws JSONException 
     */
    @Path("/createAccount")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createAccount(JsonObject body) throws JSONException {
        String userVisible;
        String username;
        String pword;
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject exists = createErrorMessage("Username already exists.", 404);
        
        try {
            userVisible = body.getString("user");
            username = body.getString("username");
            pword = body.getString("pword");
        } catch (Exception e) {
            return postResponse(invalid);   
        }
        
        if (userVisible==null || username==null || pword==null) {
            return postResponse(invalid);
        }
        
        Client c = new Client(userVisible, username, pword);
        long id = db.apiCreateAccount(c);
        
        if (id==-1) {
            return postResponse(exists);
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return postResponse(res);
        }
    }
    
    /**
     * Get information about the createProject resource
     * @param body
     * @return JSON message with the available options 
     */
    @Path("/createProject")
    @OPTIONS
    @Produces("application/json")
    public Response optCP(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to create a project
     * @param body
     * @return JSON message with the create project result (success, invalid parameters, ...)
     * @throws JSONException 
     */
    @Path("/createProject")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createProject(JsonObject body) throws JSONException {
        String name;
        String desc;
        String goal;
        String date;
        String owner;
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject exists = createErrorMessage("Name already exists.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        JSONObject dateError = createErrorMessage("Invalid date.", 404);
        
        try {
            name = body.getString("name");
            desc = body.getString("desc");
            goal = body.getString("goal");
            date = body.getString("date");
            owner = body.getString("owner");
        } catch (Exception e) {  
            return postResponse(invalid);   
        }
        
        if (name==null || desc==null || goal==null || date==null ||
                name.equals("") || desc.equals("") || goal.equals("") ||
                date.equals("")) {
            
            return postResponse(invalid);
        }
        
        double goalValue;
        long ownerId;
        Date end;
        
        try {
            goalValue = Double.parseDouble(goal);
            ownerId = Long.parseLong(owner);
            end = Date.valueOf(date);
        } catch (NumberFormatException e) {
            return postResponse(invalid);
        } catch (IllegalArgumentException e) {
            return postResponse(invalid);
        }
        
        if (end.before(new Date(Calendar.getInstance().getTime().getTime()))) {
            return postResponse(dateError);
        }
        
        Project newProject = new Project(name, desc, goalValue, end, ownerId);
        long id = db.apiCreateProject(newProject);
        
        if (id==-1) {
            return postResponse(exists);
        } else if (id==-2) {
            return postResponse(badUser);
        } else {
            JSONObject res = new JSONObject();
            res.put("id", id);
            return postResponse(res);
        }  
    }
    
    /**
     * Get information about the donate resource
     * @param body
     * @return JSON message with the available options
     */
    @Path("/donate")
    @OPTIONS
    @Produces("application/json")
    public Response optD(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to donate to a project
     * @param body
     * @return JSON message with donate result
     * @throws JSONException 
     */
    @Path("/donate")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response donate(JsonObject body) throws JSONException {
        String amt;
        String pid;
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        
        try {
            amt = body.getString("amount");
            pid = body.getString("project");
        } catch (Exception e) {
            return postResponse(invalid);   
        }
        
        if (amt==null || pid==null || amt.equals("") || pid.equals("")) {
            return postResponse(invalid);
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return postResponse(invalid);
        }
        
        boolean donate = db.apiDonate(amtValue, pidValue);
        
        if (donate) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return postResponse(res);
        } else {
            return postResponse(badProject);
        }
    }
    
    /**
     * Get information about the addMilestone resource
     * @param body
     * @return JSON message with the available options
     */
    @Path("/addMilestone")
    @OPTIONS
    @Produces("application/json")
    public Response optAM(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to add milestone on a project
     * @param body
     * @return JSON message with the add milestone result
     * @throws JSONException 
     */
    @Path("/addMilestone")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addMilestone(JsonObject body) throws JSONException {
        String amt;
        String desc;
        String pid;     
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        
        try {
            amt = body.getString("amount");
            desc = body.getString("desc");
            pid = body.getString("project");
        } catch (Exception e) {
            return postResponse(invalid);   
        }
        
        if (amt==null || desc==null || pid==null || amt.equals("") ||
                desc.equals("") || pid.equals("")) {
            return postResponse(invalid);
        }
        
        double amtValue;
        long pidValue;
        
        try {
            amtValue = Double.parseDouble(amt);
            pidValue = Long.parseLong(pid);
        } catch (NumberFormatException e) {
            return postResponse(invalid);
        }
        
        boolean add = db.apiAddMilestone(amtValue, desc, pidValue);
        
        if (add) {
            JSONObject res = new JSONObject();
            res.put("status", "success");
            return postResponse(res);
        } else {
            return postResponse(badProject);
        }
    }
    
    /**
     * Get information about the addComment resource
     * @param body
     * @return JSON message with the available options 
     */
    @Path("/addComment")
    @OPTIONS
    @Produces("application/json")
    public Response optAC(JsonObject body) {
        return optResponse();
    }
    
    /**
     * Send request to add a comment to a project
     * @param body
     * @return JSON message with the add comment result
     * @throws JSONException 
     */
    @Path("/addComment")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addComment(JsonObject body) throws JSONException {
        String content;
        String uid;
        String pid;  
        
        JSONObject invalid = createErrorMessage("Invalid Parameters!", 404);
        JSONObject badProject = createErrorMessage("Project does not exist.", 404);
        JSONObject badUser = createErrorMessage("User does not exist.", 404);
        
        try {
            content = body.getString("content");
            uid = body.getString("user");
            pid = body.getString("project");
        } catch (Exception e) {
            return postResponse(invalid);   
        }
        
        if (content==null || uid==null || pid==null || content.equals("") ||
                uid.equals("") || pid.equals("")) {
            return postResponse(invalid);
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
                return postResponse(res);
            case -1:
                return postResponse(badUser);
            default:
                return postResponse(badProject);
        }
    }
    
    /**
     * Get login result
     * @param user
     * @param pword
     * @return JSON message with the login results(invalid,not existing)
     * @throws JSONException 
     */
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
    
    /**
     * Create a Response to a get resource
     * @param json
     * @return 
     */
    private Response getResponse(JSONObject json) {
        String ha = "Access-Control-Allow-Origin";
        String hb = "*";
        String hx = "Access-Control-Allow-Methods";
        String hy = "POST, GET, OPTIONS, PUT"; 
        return Response.status(200).header(ha, hb).header(hx, hy).entity(json.toString()).build();
    }
    
    /**
     * Create a Response to a post resource
     * @param json
     * @return 
     */
    private Response postResponse(JSONObject json) {
        String ha = "Access-Control-Allow-Origin";
        String hb = "*";
        String hx = "Access-Control-Allow-Methods";
        String hy = "POST, GET, OPTIONS, PUT"; 
        return Response.status(201).header(ha, hb).header(hx, hy).entity(json.toString()).build();
    }
    
    /**
     * Create a Response to a option request
     * @return 
     */
    private Response optResponse() {
        String ha = "Access-Control-Allow-Origin";
        String hb = "*";
        String hx = "Access-Control-Allow-Methods";
        String hy = "POST, GET, OPTIONS, PUT"; 
        String hk = "Access-Control-Allow-Headers";
        String hw = "Content-Type";
        return Response.ok().header(ha, hb).header(hx, hy).header(hk,hw).build();
    }
   
    /**
     * Format the money values
     * @param original
     * @return string with the formated value
     */
    private String moneyFormat(double original) {
        return String.format("%.2f", original);
    }
    
    /**
     * Format the money values
     * @param original
     * @return double with the formated value
     */
    private double moneyFormatAsDouble(double original) {
        return Double.parseDouble(moneyFormat(original));
    }
    
    /**
     * Format the projects descriptions
     * @param original
     * @return String with the formated descriptions
     */
    private String cleanDescription(String original) {
        return original.replace("\t", "\\t")
                       .replace("\r", "\\r")
                       .replace("\n", "\\n");
    }
    
    /**
     * Create a JSON error message
     * @param message
     * @param code
     * @return JSONObject error
     * @throws JSONException 
     */
    private JSONObject createErrorMessage(String message, int code) throws JSONException{
        JSONObject error = new JSONObject();
        JSONObject tmp = new JSONObject();
        
        tmp.put("code", code);
        tmp.put("message", message);
        
        error.put("error", tmp);
        
        return error;
    }
}
