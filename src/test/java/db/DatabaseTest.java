package db;

import com.mycompany.gocommunity.DatabaseHandler;
import java.sql.Date;
import java.util.Calendar;
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
 * Set of Database tests 
 */
public class DatabaseTest {
    
    EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/ut.odb");
        EntityManager em = emf.createEntityManager();
        
    DatabaseHandler db = new DatabaseHandler("ut.odb");
    
    public DatabaseTest() {
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

    @Test
    public void testInsertDelete() {
        //System.out.println("insert/delete");
        TestEntityClass t = new TestEntityClass("hi");
        TestEntityClass t2 = new TestEntityClass("hello");
        em.getTransaction().begin();
        em.persist(t);
        em.persist(t2);
        em.getTransaction().commit();
        TypedQuery<TestEntityClass> query = em.createQuery(
            "SELECT g FROM TestEntityClass g ORDER BY g.id", TestEntityClass.class);
        List<TestEntityClass> list = query.getResultList();
        assertEquals(2, list.size());
        assertNotEquals(list.get(0), list.get(1));
        Query q = em.createQuery("DELETE FROM TestEntityClass");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
        TypedQuery<TestEntityClass> query2 = em.createQuery(
            "SELECT g FROM TestEntityClass g ORDER BY g.id", TestEntityClass.class);
        List<TestEntityClass> list2 = query2.getResultList();
        assertEquals(0, list2.size());
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
    public void testApiLogin() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);       
        assertTrue(db.createAccount(c));
        int i;
        i = db.tryLogin(c,"pword");
        assertEquals(0,i);
        i = db.tryLogin(c,"pw"); //2
        assertEquals(1,i); 
        db.tryLogin(c,"pw"); //1
        db.tryLogin(c,"pw"); //0
        i = db.tryLogin(c,"pw"); //blocked
        assertEquals(2,i); 
        i = db.tryLogin(c,"pword"); //blocked
        assertEquals(2,i); 
        
        killClients();
    }
    
    @Test
    public void testLogin() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        assertTrue(db.createAccount(c));
        Client r = db.login("user", "pword");
        assertEquals("Name",r.getName());
        r = db.login("user", "pw");
        assertEquals(null,r);
        
        killClients();
    }
    
    @Test
    public void testApiFollow() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        c.own(1);
        assertTrue(db.createAccount(c));
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        Project p2 = new Project("Name2","desc",350d,d,2L);
        p2.setId(2L);
        assertNotEquals(-1,db.createProject(p2));
        assertEquals(-3,db.apiChangeStance(1, 1)); //owned
        assertEquals(1,db.apiChangeStance(1, 2));
        assertEquals(0,db.apiChangeStance(1, 2));
        assertEquals(-2,db.apiChangeStance(1, 3)); //no project
        assertEquals(-1,db.apiChangeStance(2, 2)); //no client
        
        killAll();
    }
    
    @Test
    public void testApiUser() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        assertTrue(db.createAccount(c));
        assertEquals(null,db.apiGetUser(2));
        assertEquals(null,db.apiGetUser("user2"));
        assertEquals("Name",db.apiGetUser(1).getName());
        assertEquals("Name",db.apiGetUser("user").getName());
        
        killClients();
    }
    
    @Test
    public void testApiProject() {
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        assertEquals(null,db.apiGetProject(2));
        assertEquals(null,db.apiGetProject("n"));
        assertEquals("Name",db.apiGetProject(1).getName());
        assertEquals("Name",db.apiGetProject("Name").getName());
        
        killProjects();
    }
    
    @Test
    public void testPopular() {
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        Project p2 = new Project("Name2","desc",350d,d,2L);
        p2.setId(2L);
        assertNotEquals(-1,db.createProject(p2));
        assertEquals(2,db.getPopularProjects().size());
        
        killProjects();
    }
    
    @Test
    public void testApiAccount() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        assertEquals(1,db.apiCreateAccount(c));
        assertEquals(-1,db.apiCreateAccount(c));
        
        killClients();
    }
    
    @Test
    public void testDonate() {
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        assertTrue(db.apiDonate(10d,1));
        assertFalse(db.apiDonate(10d,2));
        
        killProjects();
    }
    
    @Test
    public void testApiComment() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        c.own(1);
        assertTrue(db.createAccount(c));
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        assertEquals(0,db.apiAddComment(1, "text", 1));
        assertEquals(-1,db.apiAddComment(2, "text", 1));
        assertEquals(-2,db.apiAddComment(1, "text", 2));
        
        killAll();
    }
    
    @Test
    public void testMilestone() {
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        assertTrue(db.apiAddMilestone(150d, "text", 1));
        assertFalse(db.apiAddMilestone(150d, "text", 2));
        
        killProjects();
    }
    
    @Test
    public void testApiCreateProject() {
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        c.own(1);
        assertTrue(db.createAccount(c));
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertEquals(1,db.apiCreateProject(p));
        assertEquals(-1,db.apiCreateProject(p));
        Project p2 = new Project("Name2","desc",350d,d,2L);
        p2.setId(2L);
        assertEquals(-2,db.apiCreateProject(p2));
        
        killAll();
    }
    
    @Test
    public void testComment() {
        Date d = new Date(Calendar.getInstance().getTime().getTime()+1000);
        Project p = new Project("Name","desc",350d,d,1L);
        p.setId(1L);
        assertNotEquals(-1,db.createProject(p));
        Client c = new Client("Name","user","pword");
        c.setId(1L);
        c.own(1);
        Comment cmt = new Comment(c, "text");
        db.updateComment(p, cmt);      
        assertEquals(1,db.getProject(1).getComments().size());
        
        killAllWithCmt();
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
