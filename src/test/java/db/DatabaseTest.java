package db;

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
public class DatabaseTest {
    
    EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/test.odb");
        EntityManager em = emf.createEntityManager();
    
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
    public void testDB() {
        System.out.println("testDB");
        TestEntityClass t = new TestEntityClass("hi");
        TestEntityClass t2 = new TestEntityClass("hello");
        Query qu1 = em.createQuery("SELECT COUNT(p) FROM TestEntityClass p");
        long amt = (Long) qu1.getSingleResult() + 2;
        em.getTransaction().begin();
        em.persist(t);
        em.persist(t2);
        em.getTransaction().commit();
        Query qu2 = em.createQuery("SELECT COUNT(p) FROM TestEntityClass p");
        assertEquals(new Long(amt), (Long) qu2.getSingleResult());
        TypedQuery<TestEntityClass> query = em.createQuery(
            "SELECT g FROM TestEntityClass g ORDER BY g.id", TestEntityClass.class);   
        List<TestEntityClass> list = query.getResultList();
        assertEquals(new Long(amt), new Long(query.getResultList().size()));
        assertNotEquals(list.get(0), list.get(1));
        for (TestEntityClass test: list)
            System.out.println(test);
    }
    
}
