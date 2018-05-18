package db;

import com.mycompany.gocommunity.ComBean;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Carlos
 */
public class BeanTest {
    
    EntityManager em;
    ComBean bean;
    
    public BeanTest() {
        EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/test.odb");
        em = emf.createEntityManager();        
    
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /*@Test
    public void reset() {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        q.executeUpdate();
        q2.executeUpdate();
        em.getTransaction().commit();
    }*/

   @Test
    public void testClientProject() {
        bean = new ComBean("test.odb");
        
        bean.setName("User");
        bean.setUsername("user");
        bean.setPassword("pw");
        String res = bean.createAccount();
        System.out.println("client: "+bean.getCreateAccountErrorMessage());
        assertEquals("main.xhtml",res);
        
        bean.setProjName("Name");
        bean.setProjDesc("desc");
        bean.setProjEndsString("2026-12-12");

        
        String res2 = bean.createProject();
        System.out.println("project: "+bean.getCreateProjectErrorMessage());
        assertEquals("newProject.xhtml",res2);
        
        bean.setProjGoalString("500000");
        String res3 = bean.createProject();
        System.out.println("project: "+bean.getCreateProjectErrorMessage());
        assertEquals("main.xhtml",res3);
        assertEquals(1,bean.getUser().getOwns().size());
        
        bean.goToOwnedProjectPage((byte) 0);
        
        bean.setMilestoneKey("250");
        bean.setMilestoneText("new");
        bean.addMilestone();
        bean.setMilestoneKey("550");
        bean.setMilestoneText("newest");
        bean.addMilestone();
        assertEquals(2, bean.getActiveProject().getAmountOfMilestones());
        
        bean.setDonation("2450.25");
        bean.donate();
        assertEquals(2450.25, bean.getActiveProject().getProgress(), 0.001);
        
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        int a = q.executeUpdate();
        int b = q2.executeUpdate();
        em.getTransaction().commit();
        assertEquals(1,a);
        assertEquals(1,b);
    }
    
    @Test
    public void testSearch() {
        bean = new ComBean("test.odb");
        
        bean.setName("User2");
        bean.setUsername("user2");
        bean.setPassword("pw");
        String res = bean.createAccount();
        assertEquals("main.xhtml",res);
        
        bean.setProjName("Name2");
        bean.setProjDesc("desc");
        bean.setProjEndsString("2026-12-12");
        bean.setProjGoalString("500000");
        String res2 = bean.createProject();
        assertEquals("main.xhtml",res2);
        
        bean.setSearch("x");
        List<Project> list = bean.searchProjects();
        assertEquals(0, list.size());
        bean.setSearch("am");
        list = bean.searchProjects();
        assertEquals(1, list.size());
        assertEquals("desc", list.get(0).getDescription());
        
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        int a = q.executeUpdate();
        int b = q2.executeUpdate();
        em.getTransaction().commit();
        assertEquals(1,a);
        assertEquals(1,b);    
    }
    
    @Test
    public void testComment() throws ParseException {
        bean = new ComBean("test.odb");
        
        bean.setName("User3");
        bean.setUsername("user3");
        bean.setPassword("pw");
        String res = bean.createAccount();
        assertEquals("main.xhtml",res);
        
        bean.setProjName("Name3");
        bean.setProjDesc("desc");
        bean.setProjEndsString("2026-12-12");
        bean.setProjGoalString("500000");
        String res2 = bean.createProject();
        assertEquals("main.xhtml",res2);
        
        bean.goToOwnedProjectPage((byte) 0);
        
        Comment c1 = new Comment(bean.getUser(), "hi");
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy '-' h:mm a");
        String dateString = "Tue, 11 May 2010 - 8:10 PM";
        java.util.Date date = format.parse(dateString);
        c1.setDate(date);
        Comment c2 = new Comment(bean.getUser(), "hello");
        
        bean.getActiveProject().addComment(c1);
        bean.getActiveProject().addComment(c2);
        
        List<Comment> comments = bean.getActiveProject().getComments();
        assertEquals(2, comments.size());
        assertEquals(dateString, comments.get(0).getFormattedDate());
        assertEquals("User3 (user3)", comments.get(1).getHeader());
        assertTrue(c2.getDate().after(c1.getDate()));
        
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        int a = q.executeUpdate();
        int b = q2.executeUpdate();
        em.getTransaction().commit();
        assertEquals(1,a);
        assertEquals(1,b); 
    }
        
}
