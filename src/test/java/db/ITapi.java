package db;

import com.mycompany.gocommunity.DatabaseHandler;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

/**
 *
 * @author Carlos
 */
public class ITapi {
    
    private final String link = "http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data";
    //"http://localhost:8080/GoCommunity/api/data";
    
    EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/go.odb");
        EntityManager em = emf.createEntityManager();
    
    private final Random random = new Random();
    private final DatabaseHandler db = new DatabaseHandler("go.odb");
    
    private javax.ws.rs.client.Client client;
    private WebTarget target;
    
    @Before
    public void initClient() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(link);
    }
    
    @Test
    public void testGetUserInfo() throws JSONException{
        System.out.println("testGetUserInfo");
        String uName;
        JSONObject reply;
        do { //randomly generate username until an available one is found
            uName = randomName();
        } while (db.apiGetProject(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", uName);
        reply = postJson(json, "/createAccount");
        assertTrue(reply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        json.put("username", uName);
        json.put("pword", uName);
        reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject user = getJson("/user/"+uName);
        assertFalse(user.length() == 0);
        assertTrue(user.getString("name").startsWith(uName));
        
        killClient(uName);
    }
    
    @Test
    public void testGetNonExistentUser() throws JSONException{
        System.out.println("testGetNonExistentUser");
        String username;
        do { //randomly generate username until an available one is found
            username = randomName();
        } while (db.apiGetProject(username)!=null);
        
        JSONObject user = getJson("/user/"+username);
        
        assertFalse(user.length() == 0);
        assertTrue(user.getJSONObject("error").getString("message").startsWith("Not Found!"));
    }
    
    @Test
    public void testGetPopularProjects() throws JSONException{
        System.out.println("testGetPopularProjects");
        JSONObject popular = getJson("/popular");
        //assertFalse(popular.getJSONArray("list").length() != 0);
    }
    
    @Test
    public void testGetProjectInfo() throws JSONException{
        System.out.println("testGetProjectInfo");
        String pName, uName;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "9000");
        json2.put("date", "2019-01-01");
        json2.put("owner", "1");
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        
        
        JSONObject project = getJson("/project/"+pName);
        assertFalse(project.length() == 0);
        assertTrue(project.getString("name").startsWith(pName));
        assertTrue(project.getString("description").startsWith("randomText"));
        
        killProject(pName);
        killClient(uName);
    }
    
    @Test
    public void testGetNonExistentProjectInfo() throws JSONException{
        System.out.println("testGetNonExistentProjectInfo");
        String pName;
        do { //randomly generate project until an available one is found
            pName = randomName();
        } while (db.apiGetProject(pName)!=null);
        
        JSONObject project = getJson("/project/"+pName);

        assertFalse(project.length() == 0);
        assertTrue(project.getJSONObject("error").getString("message").startsWith("Not Found!"));
    }
    
    @Test
    public void testGetSearchProject() throws JSONException{
        System.out.println("testGetSearchProject");
        String pName, uName;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "100");
        json2.put("date", "2019-08-01");
        json2.put("owner", reply.getString("id"));
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        // Search project by name and id
        JSONObject replyId = getJson("/project/"+reply2.getInt("id"));
        assertTrue(replyId.has("id"));
        
        JSONObject project = getJson("/search/"+pName);
        assertFalse(project.length() == 0);
        assertTrue(project.getJSONArray("list").getJSONObject(0).getString("name").startsWith(pName));
        assertTrue(project.getJSONArray("list").getJSONObject(0).getString("description").startsWith("randomText"));
        
        killProject(pName);
        killClient(uName);
    }
    
    @Test
    public void testFollowProject() throws JSONException{
        System.out.println("testFollowProject");
        String pName, uName;
        JSONObject fReply;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "100");
        json2.put("date", "2019-08-01");
        json2.put("owner", reply.getString("id"));
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        
        JSONObject fJson = new JSONObject();
        fJson.put("user", reply.getString("id"));
        fReply = postJson(fJson ,"/follow");
        assertTrue(fReply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        fJson.put("project", reply2.getString("id"));
        
        fReply = postJson(fJson ,"/follow");
        assertTrue(fReply.getJSONObject("error").getString("message").startsWith("User is project owner."));
        
        killProject(pName);
        /*fReply = postJson(fJson, "/donate");
        assertTrue(fReply.getJSONObject("error").getString("message").startsWith("Project does not exist."));*/
        killClient(uName);
        /*fReply = postJson(fJson, "/donate");
        assertTrue(fReply.getJSONObject("error").getString("message").startsWith("User does not exist."));*/
    }
    
    @Test
    public void testCreateGetAccount() throws JSONException {  
        System.out.println("testCreateGetAccount");
        String uname;
        String uname2;
        JSONObject reply;
        
        do { //randomly generate username until an available one is found
            uname = randomName();
            uname2 = randomName();
        } while (db.apiGetUser(uname)!=null || db.apiGetUser(uname2)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uname);
        reply = postJson(json, "/createAccount");
        assertTrue(reply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        json.put("pword", (String) null);
        reply = postJson(json, "/createAccount");
        assertTrue(reply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        json.put("pword", "pw");
        JSONObject real = postJson(json, "/createAccount");
        assertTrue(real.has("id"));
        reply = postJson(json, "/createAccount");
        assertTrue(reply.getJSONObject("error").getString("message").startsWith("Username already exists."));
        
        reply = getJson("/user/"+uname2);
        assertTrue(reply.getJSONObject("error").getString("message").startsWith("Not Found!"));
        reply = getJson("/user/"+uname);
        assertTrue(reply.has("id"));
        reply = getJson("/user/"+real.getInt("id"));
        assertTrue(reply.has("id"));
        
        killClient(uname);
    }
    
    @Test
    public void testDonateToProject() throws JSONException{
        System.out.println("testDonateToProject");
        String pName, uName;
        JSONObject dReply;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "100");
        json2.put("date", "2019-08-01");
        json2.put("owner", reply.getString("id"));
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        
        JSONObject dJson = new JSONObject();
        dJson.put("amount", "123");
        dReply = postJson(dJson, "/donate");
        assertTrue(dReply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        dJson.put("project", reply2.getString("id"));
        dReply = postJson(dJson, "/donate");
        assertTrue(dReply.getString("status").startsWith("success"));
        
        killProject(pName);
        killClient(uName);
        
        /*dReply = postJson(dJson, "/donate");
        assertTrue(dReply.getJSONObject("error").getString("message").startsWith("Project does not exist."));*/
    }
    
    @Test
    public void testAddMilestone() throws JSONException{
        System.out.println("testDonateToProject");
        String pName, uName;
        JSONObject mReply;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "100");
        json2.put("date", "2019-08-01");
        json2.put("owner", reply.getString("id"));
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        
        JSONObject mJson = new JSONObject();
        mJson.put("amount", "100");
        mReply = postJson(mJson, "/addMilestone");
        assertTrue(mReply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        mJson.put("desc", "randomText");
        mJson.put("project", reply2.getString("id"));

        mReply = postJson(mJson, "/addMilestone");
        assertTrue(mReply.getString("status").startsWith("success"));
        
        killProject(pName);
        killClient(uName);
        
        /*mReply = postJson(mJson, "/addMilestone");
        assertTrue(mReply.getJSONObject("error").getString("message").startsWith("Project does not exist."));*/
    }
    
    @Test
    public void testAddComment() throws JSONException{
        System.out.println("testDonateToProject");
        String pName, uName;
        JSONObject cReply;
        do { //randomly generate project until an available one is found
            pName = randomName();
            uName = randomName();
        } while (db.apiGetProject(pName)!=null | db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uName);
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "100");
        json2.put("date", "2019-08-01");
        json2.put("owner", reply.getString("id"));
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));

        JSONObject cJson = new JSONObject();
        cJson.put("content", "addingComment");
        cReply = postJson(cJson, "/addComment");
        assertTrue(cReply.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
        
        cJson.put("user", reply.getString("id"));
        cJson.put("project", reply2.getString("id"));
        cReply = postJson(cJson, "/addComment");
        assertTrue(cReply.getString("status").startsWith("success"));
        
        killProject(pName);
        /*cReply = postJson(cJson, "/addComment");
        assertTrue(mReply.getJSONObject("error").getString("message").startsWith("Project does not exist."));*/
        
        killClient(uName);
        /*cReply = postJson(cJson, "/addComment");
        assertTrue(mReply.getJSONObject("error").getString("message").startsWith("User does not exist."));*/
    }
    
    @Test
    public void testGetEmptyLoginResult() throws JSONException{
        System.out.println("testGetEmptyLoginResult");
        JSONObject login = getJson("/login");
        
        assertFalse(login.length() == 0);
        assertTrue(login.getJSONObject("error").getString("message").startsWith("Invalid Parameters!"));
    }
    
    @Test
    public void testGetLoginResult() throws JSONException{
        System.out.println("testGetLoginResult");
        String uName;
        do { //randomly generate username until an available one is found
            uName = randomName();
        } while (db.apiGetUser(uName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "test");
        json.put("username", uName);
        json.put("pword", "test");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        Response response = target.path("/login").queryParam("user", uName).queryParam("pword", "test").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        
        String login = response.readEntity(String.class);
        JSONObject log = new JSONObject(login);
        assertFalse(log.length() == 0);
        
        assertTrue(log.getString("status").startsWith("true"));
        
        killClient(uName);
    }
    
    @Test
    public void testOptCreateAccount() {
        System.out.println("testOptCreateAccount");
        Response response = this.target.path("/createAccount").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    @Test
    public void testOptFollow(){
        System.out.println("testOptFollow");
        Response response = this.target.path("/follow").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    @Test
    public void testOptCreateProject(){
        System.out.println("testOptCreateProject");
        Response response = this.target.path("/createProject").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    @Test
    public void testOptDonate(){
        System.out.println("testOptDonate");
        Response response = this.target.path("/donate").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    @Test
    public void testOptAddMilestone(){
        System.out.println("testOptAddMilestone");
        Response response = this.target.path("/addMilestone").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    @Test
    public void testOptAddComment(){
        System.out.println("testOptAddComment");
        Response response = this.target.path("/addComment").request(MediaType.APPLICATION_JSON).options();
        assertThat(response.getStatus(), CoreMatchers.is(200));
    }
    
    private void killClient(String n) {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Client c WHERE c.username=:n");
        q.setParameter("n", n);
        int rows = q.executeUpdate();
        //System.out.println("Rows : "+rows);
        em.getTransaction().commit();
    }
    
    private void killProject(String n) {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project c WHERE c.name=:n");
        q.setParameter("n", n).executeUpdate();
        em.getTransaction().commit();
    }
    
    private JSONObject getJson(String path) throws JSONException{
        initClient();
        Response response = target.path(path).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        String res = response.readEntity(String.class);
        return new JSONObject(res);
    }
    
    private JSONObject postJson(JSONObject json, String path) throws JSONException {
        initClient();
        Entity e = Entity.json(json.toString());
        Response response = this.target.path(path).request(MediaType.APPLICATION_JSON).post(e);
        assertThat(response.getStatus(), CoreMatchers.is(201));
        String res = response.readEntity(String.class);      
        return new JSONObject(res);
    }

    private int random(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }
    
    private String randomName() {
        int type;
        StringBuilder sb = new StringBuilder("Rest-");
        
        for (int i=0;i<20;i++) {
            type = random.nextInt(3);
            switch (type) {
                case 0:
                    sb.append(random(0,10));
                    break;
                case 1:
                    sb.append((char) random(65,90));
                    break;
                default:
                    sb.append((char) random(97,122));
                    break;
            }
        }
        return sb.toString();    
    }   
}