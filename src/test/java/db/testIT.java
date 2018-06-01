package db;

import com.mycompany.gocommunity.ApiBean;
import com.mycompany.gocommunity.DatabaseHandler;
import java.sql.Date;
import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Test;
import db.Client;
import db.Comment;
import db.Project;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
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
    
    @Test
    public void testOpts() {
        System.out.println("IT 0");
    
        Response r;
        init();
        r = this.target.path("/createAccount").request(MediaType.APPLICATION_JSON).options();
        assertEquals(200,r.getStatus());
        init();
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
        assertEquals(200,r.getStatus());
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
    
    @Test
    public void testPopular() throws JSONException {
        System.out.println("IT 2");
        
        JSONObject reply = getJson("/popular");
        assertTrue(reply.has("list"));
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
    
    private JSONObject getJson(String path) throws JSONException {
        init();
        Response r = target.path(path).request(MediaType.APPLICATION_JSON).get();
        String res = r.readEntity(String.class);      
        return new JSONObject(res);
    }
    
    private JSONObject postJson(JSONObject json, String path) throws JSONException {
        Entity e = Entity.json(json.toString());
        init();
        Response r = this.target.path(path).request(MediaType.APPLICATION_JSON).post(e);
        String res = r.readEntity(String.class);      
        return new JSONObject(res);
    }
    
    private void init() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(link);
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
