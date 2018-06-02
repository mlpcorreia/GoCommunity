package db;

import com.mycompany.gocommunity.DatabaseHandler;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class testIT {
    
    private String link = "http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data";
    //"http://localhost:8080/GoCommunity/api/data
    
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
        String rUsername;
        do { //randomly generate username until an available one is found
            rUsername = randomName();
        } while (db.apiGetProject(rUsername)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", rUsername);
        json.put("username", rUsername);
        json.put("pword", rUsername);
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject user = getJson("/user/"+rUsername);
        assertFalse(user.length() == 0);
        assertTrue(user.getString("name").startsWith(rUsername));
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
        assertFalse(popular.getJSONArray("list").length() == 0);
    }
    
    @Test
    public void testGetProjectInfo() throws JSONException{
        System.out.println("testGetProjectInfo");
        String pName;
        do { //randomly generate project until an available one is found
            pName = randomName();
        } while (db.apiGetProject(pName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", "IT");
        json.put("pword", "IT");
        JSONObject reply = postJson(json, "/createAccount");
        assertTrue(reply.has("id"));
        
        JSONObject json2 = new JSONObject();
        json2.put("name", pName);
        json2.put("desc", "randomText");
        json2.put("goal", "9000");
        json2.put("date", "2018-01-01");
        json2.put("owner", "1");
        JSONObject reply2 = postJson(json2, "/createProject");
        assertTrue(reply2.has("id"));
        
        
        JSONObject project = getJson("/project/"+pName);
        assertFalse(project.length() == 0);
        assertTrue(project.getString("name").startsWith(pName));
        assertTrue(project.getString("description").startsWith("randomText"));
        
        killProject(pName);
        killClient("IT");
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
        String pName;
        do { //randomly generate project until an available one is found
            pName = randomName();
        } while (db.apiGetProject(pName)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", "IT");
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
        
        JSONObject project = getJson("/search/"+pName);
        assertFalse(project.length() == 0);
        assertTrue(project.getJSONArray("list").getJSONObject(0).getString("name").startsWith(pName));
        assertTrue(project.getJSONArray("list").getJSONObject(0).getString("description").startsWith("randomText"));
        
        killProject(pName);
        killClient("IT");
    }
    
    @Test
    public void testOpts() {
        System.out.println("IT 0");
    
        Response r;
        //init();
        r = this.target.path("/createAccount").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        /*init();
        r = this.target.path("/follow").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        init();
        r = this.target.path("/createProject").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        init();
        r = this.target.path("/donate").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        init();
        r = this.target.path("/addMilestone").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        init();
        r = this.target.path("/addComment").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());*/
    }
    
    @Test
    public void testCreateGetAccount() throws JSONException {  
        System.out.println("IT 1");
        
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
        assertEquals("Invalid Parameters!",reply.getJSONObject("error").get("message"));
        json.put("pword", (String) null);
        reply = postJson(json, "/createAccount");
        assertEquals("Invalid Parameters!",reply.getJSONObject("error").get("message"));
        json.put("pword", "pw");
        JSONObject real = postJson(json, "/createAccount");
        assertTrue(real.has("id"));
        reply = postJson(json, "/createAccount");
        assertEquals("Username already exists.",reply.getJSONObject("error").get("message"));
        
        reply = getJson("/user/"+uname2);
        assertEquals("Not Found!",reply.getJSONObject("error").get("message"));
        reply = getJson("/user/"+uname);
        assertTrue(reply.has("id"));
        reply = getJson("/user/"+real.getInt("id"));
        assertTrue(reply.has("id"));
        
        killClient(uname);
    }
      
    /*@Test
    public void testCreateGetProject() throws JSONException {   //not finished
        System.out.println("IT 1");
        
        String uname;
        String pname;
        String pname2;
        JSONObject reply;
        
        do { //randomly generate username until an available one is found
        uname = randomName();
        pname = randomName();
        pname2 = randomName();
        } while (db.apiGetUser(uname)!=null || db.apiGetProject(pname)!=null || db.apiGetProject(pname2)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uname);
        json.put("pword", "pw");
        JSONObject realuser = postJson(json, "/createAccount");
        assertTrue(realuser.has("id"));
        
        reply = postJson(json, "/createAccount");
        assertEquals("Username already exists.",reply.getJSONObject("error").get("message"));
        
        reply = getJson("/project/"+pname2);
        assertEquals("Not Found!",reply.getJSONObject("error").get("message"));
        reply = getJson("/project/"+pname);
        assertTrue(reply.has("id"));
        reply = getJson("/project/"+real.getInt("id"));
        assertTrue(reply.has("id"));
        
        killClient(uname);
    }*/
    
    private void killClient(String n) {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Client c WHERE c.username=:n");
        q.setParameter("n", n).executeUpdate();
        em.getTransaction().commit();
    }
    
    private void killProject(String n) {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project c WHERE c.name:n");
        q.setParameter("n", n).executeUpdate();
        em.getTransaction().commit();
    }
    
    private JSONObject getJson(String path) throws JSONException{
        Response response = target.path(path).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        String res = response.readEntity(String.class);
        return new JSONObject(res);
    }
    
    private JSONObject postJson(JSONObject json, String path) throws JSONException {
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
        StringBuilder sb = new StringBuilder();
        
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
