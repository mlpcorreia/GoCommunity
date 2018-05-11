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
    public void testInsertDelete() {
        System.out.println("insert/delete");
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
    
}
