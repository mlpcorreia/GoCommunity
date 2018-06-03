package db;

import com.mycompany.gocommunity.ComBean;
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
   public void testBeanEquality() {
       ComBean b = new ComBean();
       ComBean c = new ComBean("g.odb");
       assertNotEquals(b,c);
   }
   
   @Test
   public void testLogin() {
        bean = new ComBean("test.odb");
       
        bean.setName("User");
        bean.setUsername("user");
        bean.setPassword("pw");
        assertEquals("main.xhtml",bean.createAccount());
        
        assertEquals("main.xhtml",bean.login());
        bean.setUsername("user2");
        assertEquals("login.xhtml",bean.login());
        assertEquals("Account not found.",bean.getLoginErrorMessage());
        
        killClients();
   }
   
   @Test
   public void testCreateAccount() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("User",bean.getName());
       assertEquals("user",bean.getUsername());
       assertEquals("pw",bean.getPassword());
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setUsername("");
       assertEquals("newAccount.xhtml",bean.createAccount());
       assertEquals("Every field is required.",bean.getCreateAccountErrorMessage());
       bean.setUsername("user");
       assertEquals("newAccount.xhtml",bean.createAccount());
       assertEquals("Username already exists.",bean.getCreateAccountErrorMessage());
       
       killClients();
   }
   
   @Test
   public void testCreateProject() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("");
       assertEquals("Name",bean.getProjName());
       assertEquals("desc",bean.getProjDesc());
       assertEquals("2026-12-12",bean.getProjEndsString());
       assertEquals("",bean.getProjGoalString());
       
       assertEquals("newProject.xhtml",bean.createProject());
       assertEquals("Every field is required.",bean.getCreateProjectErrorMessage());
       bean.setProjGoalString("x");
       assertEquals("newProject.xhtml",bean.createProject());
       assertEquals("Please insert a valid number in the \"goal\" field.",bean.getCreateProjectErrorMessage());
       bean.setProjGoalString("200");
       bean.setProjEndsString("2026-12-12x");
       assertEquals("newProject.xhtml",bean.createProject());
       assertEquals("Please respect the date syntax.",bean.getCreateProjectErrorMessage());
       bean.setProjEndsString("2006-12-12");
       assertEquals("newProject.xhtml",bean.createProject());
       assertEquals("Expiration date cannot be earlier than creation date.",bean.getCreateProjectErrorMessage());
       bean.setProjEndsString("2026-12-12");
       assertEquals("main.xhtml",bean.createProject());
       assertEquals("newProject.xhtml",bean.createProject());
       assertEquals("A project with this name already exists.",bean.getCreateProjectErrorMessage());
       
       killAll();
   }
   
   @Test
   public void testSearchProjects() {
       bean = new ComBean("test.odb");
       
       bean.setSearch("");
       assertEquals("",bean.getSearch());
       assertEquals(0,bean.searchProjects().size());
       assertEquals("Please insert a search term.",bean.getSearchErrorMessage());
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());
       
       bean.setSearch("am");     
       assertEquals(1,bean.searchProjects().size());
       assertTrue(bean.isEmptyString(bean.getSearchErrorMessage()));
       
       assertEquals("project.xhtml",bean.goToSearchedProjectPage((byte) 0));
       
       killAll();
   }
   
   @Test
   public void testComment() {
       bean = new ComBean("test.odb");
       
       bean.setCommentText("");
       assertEquals("",bean.getCommentText());
       bean.addComment();
       assertEquals("Comments cannot be empty!",bean.getCommentErrorMessage());
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());
       bean.goToOwnedProjectPage((byte) 0);
       bean.setCommentText("test");
       bean.addComment();
       assertEquals(1,bean.getActiveProject().getComments().size());
       
       killAllWithCmt();
   }
   
   @Test
   public void testDonate() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());
       
       bean.goToOwnedProjectPage((byte) 0);
       
       bean.setDonation("");
       assertEquals("",bean.getDonation());
       bean.donate();
       assertEquals("Please insert a monetary value.",bean.getDonationErrorMessage());
       bean.setDonation("x");
       bean.donate();
       assertEquals("Please insert a valid number in the \"donation\" field.",bean.getDonationErrorMessage());
       
       bean.setDonation("20");
       bean.donate();
       assertEquals(20,bean.getActiveProject().getProgress(),0.01);
       
       killAll();
   }    
   
   @Test
   public void testMilestone() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());
       
       bean.goToOwnedProjectPage((byte) 0);
       
       bean.setMilestoneKey("");
       bean.setMilestoneText("text");
       assertEquals("",bean.getMilestoneKey());
       assertEquals("text",bean.getMilestoneText());    
       bean.addMilestone();
       assertEquals("Both fields are required.",bean.getCreateMilestoneErrorMessage());
       bean.setMilestoneKey("x");
       bean.addMilestone();
       assertEquals("Please insert a valid number in the \"value\" field.",bean.getCreateMilestoneErrorMessage());
       
       bean.setMilestoneKey("5.052");
       bean.addMilestone();
       assertEquals(1,bean.getActiveProject().getAmountOfMilestones());
       assertEquals("5.05",bean.moneyFormat(bean.getActiveProject().getMilestoneKeys().get(0)));
       
       killAll();
   } 
   
   @Test
   public void testStance() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());

       assertEquals("project.xhtml",bean.goToOwnedProjectPage((byte) 0));
       
       bean.follow();
       assertEquals(1,bean.getUser().getFollows().size());
       assertTrue(bean.isVisitingFollowedProject());
       assertEquals("project.xhtml",bean.goToFollowedProjectPage((byte) 0));
       bean.unfollow();
       assertEquals(0,bean.getUser().getFollows().size());
       assertFalse(bean.isVisitingFollowedProject());
       
       assertTrue(bean.isVisitingOwnedProject());
       
       killAll();
   }

   @Test
   public void testClientProject() {
        bean = new ComBean("test.odb");
        
        bean.setName("User");
        bean.setUsername("user");
        bean.setPassword("pw");
        assertEquals("main.xhtml",bean.createAccount());
        
        bean.setProjName("Name");
        bean.setProjDesc("desc");
        bean.setProjEndsString("2026-12-12");

        assertEquals("newProject.xhtml",bean.createProject());
        
        bean.setProjGoalString("500000");
        assertEquals("main.xhtml",bean.createProject());
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
        
        killAll();
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
        assertEquals("main.xhtml",bean.createProject());
        
        bean.setSearch("x");
        List<Project> list = bean.searchProjects();
        assertEquals(0, list.size());
        bean.setSearch("am");
        list = bean.searchProjects();
        assertEquals(1, list.size());
        assertEquals("desc", list.get(0).getDescription());
        
        killAll();   
    }
    
    @Test
    public void testPopularLogic() {
       bean = new ComBean("test.odb");
       
       bean.setName("User");
       bean.setUsername("user");
       bean.setPassword("pw");
       assertEquals("main.xhtml",bean.createAccount());
       
       bean.setProjName("Name");
       bean.setProjDesc("desc");
       bean.setProjEndsString("2026-12-12");
       bean.setProjGoalString("500");
       assertEquals("main.xhtml",bean.createProject());

       assertEquals(1,bean.getPopularProjects().size());
       assertEquals("project.xhtml",bean.goToProjectPage((byte) 0));
       
       Long idpop = bean.getPopularProjects().get(0).getId();
       Long idproj = bean.getProject(Integer.parseInt(idpop.toString())).getId();
       assertEquals(idpop,idproj);
       
       killAll();
       
    }
    
    @Test
    public void testCommentLogic() throws ParseException {
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
        assertEquals("Posted by User3", comments.get(1).getHeader());
        assertTrue(c2.getDate().after(c1.getDate()));
        
        killAll(); 
    }
    
    private void killClients() {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Client");
        q.executeUpdate();
        em.getTransaction().commit();
    }
    
    private void killProjects() {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        q.executeUpdate();
        em.getTransaction().commit();
    }
    
    private void killAll() {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        q.executeUpdate();
        q2.executeUpdate();
        em.getTransaction().commit();
    }
    
    private void killAllWithCmt() {
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM Project");
        Query q2 = em.createQuery("DELETE FROM Client");
        Query q3 = em.createQuery("DELETE FROM Comment");
        q.executeUpdate();
        q2.executeUpdate();
        q3.executeUpdate();
        em.getTransaction().commit();
    }
        
}
