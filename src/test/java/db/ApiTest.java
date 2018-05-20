package db;

import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Carlos
 */
public class ApiTest {
    
    @Test
    public void testApi(){
    }
    /*private javax.ws.rs.client.Client client;
    private javax.ws.rs.client.WebTarget target;
    
   @Before
    public void initClient(){
        this.client = ClientBuilder.newClient();
        this.target = client.target("http://localhost:8080/GoCommunity/api/data");
    }
    
    @Test
    public void testGetUserInfo() {
        Response response = target.path("/user/1").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        
        JsonObject user = response.readEntity(JsonObject.class);
        assertFalse(user.isEmpty());
        assertTrue(user.getString("username").startsWith("test"));
    }
    
    @Test
    public void testGetNonExistentUser(){
        Response response = target.path("/user/null").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(404));
        
        JsonObject user = response.readEntity(JsonObject.class);
        assertFalse(user.isEmpty());
        assertTrue(user.getJsonObject("error").getString("message").startsWith("Not Found!"));
    }
    
    @Test
    public void testGetPopularProjects(){
        Response response = target.path("/popular").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        
        JsonObject popular = response.readEntity(JsonObject.class);
        assertFalse(popular.getJsonArray("list").isEmpty());
    }
    
    @Test
    public void testGetProjectInfo(){
        Response response = target.path("/project/test").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(200));
        
        JsonObject project = response.readEntity(JsonObject.class);
        assertFalse(project.isEmpty());
        assertTrue(project.getString("name").startsWith("test"));
    }
    
    @Test
    public void testGetNonExistentProjectInfo(){
        Response response = target.path("/project/null").request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), CoreMatchers.is(404));
        
        JsonObject project = response.readEntity(JsonObject.class);
        assertFalse(project.isEmpty());
        assertTrue(project.getJsonObject("error").getString("message").startsWith("Not Found!"));
    }*/
}
