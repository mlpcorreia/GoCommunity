package db;

import java.sql.Date;
import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Carlos
 */
public class EntityTest {
    
    @Test
    public void testDummyEntity() {
        TestEntityClass t = new TestEntityClass();
        t.setId(1L);
        t.setWord("x");
        assertEquals((long) 1,(long) t.getId());
        assertEquals("x",t.getWord());
        assertEquals(new Long(1).hashCode(),t.hashCode());
        TestEntityClass t2 = new TestEntityClass();
        t2.setId(1L);
        t2.setWord("x");
        assertTrue(t.equals(t2));
        assertFalse(t.equals(1));
        assertEquals("db.TestEntityClass[ id=1 ]",t.toString());
    }
    
    @Test
    public void testComment() {
        Comment c = new Comment();
        c.setId(1L);
        c.setContent("text");
        c.setDate(new Date(1527699930092L)); //wed 30/5/2018 18:05
        Project p = new Project();
        p.setId(1L);
        c.setProject(p);
        c.setUserVisibleName("user");
        assertEquals((long) 1,(long) c.getId());
        assertEquals((long) 1,(long) c.getProject().getId());
        assertEquals("user",c.getUserVisibleName());
        assertEquals("Wed, 30 May 2018",c.getApiFormattedDate());
        assertEquals("text",c.getContent());
    }
    
    @Test
    public void testClient() {
        Client c = new Client();
        c.setId(1L);
        c.setName("name");
        c.setPassword("pw");
        c.setUsername("username");
        Client c2 = new Client();
        c2.setId(1L);
        c2.setName("name");
        c2.setPassword("pw");
        c2.setUsername("username");
        c.setLastBadLogin(Calendar.getInstance().getTime().getTime()); //now
        c.setLoginTries(3);
        assertTrue(c.canLogIn());
        c.setLastBadLogin(100); //24h lock
        c.setLoginTries(0);
        assertTrue(c.canLogIn());
        assertEquals(new Long(1).hashCode(),c.hashCode());
        assertTrue(c.equals(c2));
        assertFalse(c.equals(1));
        assertEquals("db.Client[ id=1 ]",c.toString());
    }
    
    @Test
    public void testProject() {
        Project p = new Project();
        p.initValues();
        p.setId(1L);
        p.setName("name");
        p.setDescription("desc");
        p.setGoal(300d);
        p.setCreatedOn(Date.valueOf("2018-10-10"));
        p.setEndsOn(Date.valueOf("2018-12-12"));
        p.setOwner(1L);
        p.addToProgress(25d);
        assertEquals(300.0,p.getGoal(),0.01);
        assertEquals("name - 25.00€/300.00€ - Ends on 2018-12-12",p.getOverview());
        assertEquals("25.00€",p.printFormattedProgress());
        assertEquals("300.00€",p.printFormattedGoal());
        p.followedBy(1);
        assertEquals(1,p.getFollowers().size());
        p.unfollowedBy(1);
        assertEquals(0,p.getFollowers().size());
        p.addMilestone(20.25, "text");
        assertEquals(20.25,p.getMilestoneKeys().get(0),0.01);
        assertEquals("text",p.getMilestoneText(20.25));
        assertTrue(p.getEndsOn().after(p.getCreatedOn()));
        Project p2 = new Project();
        p2.initValues();
        p2.setId(1L);
        p2.setName("name");
        p2.setDescription("desc");
        p2.setGoal(300d);
        p2.setCreatedOn(Date.valueOf("2018-10-10"));
        p2.setEndsOn(Date.valueOf("2018-12-12"));
        p2.setOwner(1L);
        p2.addToProgress(25d);
        assertEquals(new Long(1).hashCode(),p.hashCode());
        assertTrue(p.equals(p2));
        assertFalse(p.equals(1));
        assertEquals("db.Project[ id=1 ]",p.toString());
    }
}
