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
    //"http://localhost:8080/GoCommunity/api/data";
    
    private final Random random = new Random();
    private final DatabaseHandler db = new DatabaseHandler("go.odb");
    
    private javax.ws.rs.client.Client client;
    private WebTarget target;
    
    /*@Test
    public void testOpts() {     
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
    }*/
    
    /*@Test
    public void testCreateGetAccount() throws JSONException {  
        String uname; 
        Response r;
        
        do { //randomly generate username until an available one is found
        uname = randomName();
        } while (db.apiGetUser(uname)!=null);
        
        JSONObject json = new JSONObject();
        json.put("user", "IT");
        json.put("username", uname);
        Entity e = Entity.json(json.toString());
        System.out.println(json.toString());
        init();
        r = this.target.path("/createAccount").request(MediaType.APPLICATION_JSON).post(e);
        String res = r.readEntity(String.class);
        System.out.println(res);
        JSONObject reply = new JSONObject(res);
        System.out.println(reply.toString());
    }*/
    
    
    
    /*@Test
    public void testOspts() throws JSONException {     
        JSONObject json = new JSONObject();
        json.put("user", randomName());
        json.put("username", randomName());
        json.put("pword", "2");
        Entity e = Entity.json(json);
        
        String s = "{\"user\":\"u\",\"username\":\"x\",\"pword\":\"a\"}";
        Entity es = Entity.json(s);
        Response r = this.target.path("/createAccount").request(MediaType.APPLICATION_JSON).post(es);
        String js = r.readEntity(String.class);
        JSONObject jj = new JSONObject(js);
        System.out.println("a: "+jj.toString());
        System.out.println("ab: "+jj.getJSONObject("error").getString("message"));
    }*/
    
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
