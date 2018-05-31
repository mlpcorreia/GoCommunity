package db;

import com.mycompany.gocommunity.ApiBean;
import java.sql.Date;
import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Test;
import db.Client;
import db.Comment;
import db.Project;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Carlos
 */
public class testIT {
    
    @Test
    public void testDummyEntityx() {
        ApiBean b= new ApiBean();
    }
    
}
