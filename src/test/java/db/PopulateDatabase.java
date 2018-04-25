package db;

import java.sql.Date;
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
public class PopulateDatabase {
    
    EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("$objectdb/db/go.odb");
        EntityManager em = emf.createEntityManager();
    
    public PopulateDatabase() {
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

   /* @Test
    public void populate() {
        System.out.println("populating");
        Client c = new Client("N","u","p");
        Project p1 = new Project("p1", "desc\ndesc", 4000.40, Date.valueOf("2020-10-08"));
        Project p2 = new Project("p2", "desc\ndesc", 3000.40, Date.valueOf("2021-07-08"));
        Project p3 = new Project("p3", "desc\ndesc", 2000.40, Date.valueOf("2022-11-08"));
        Project p4 = new Project("p4", "desc\ndesc", 1000.40, Date.valueOf("2023-12-08"));
        Project p5 = new Project("p5", "desc\ndesc", 100.40, Date.valueOf("2024-09-08"));
        p1.addToProgress(150.0);
        p2.addToProgress(3409.0);
        p3.addToProgress(1222.0);
        p4.addToProgress(166.0);
        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(p5);
        em.getTransaction().commit();
        TypedQuery<Project> query = em.createQuery(
            "SELECT g FROM Project g WHERE g.name='p1' OR g.name='p2'", Project.class);   
        List<Project> list = query.getResultList();
        TypedQuery<Project> query2 = em.createQuery(
            "SELECT g FROM Project g WHERE g.name='p3'", Project.class); 
        List<Project> list2 = query.getResultList();
        c.follow(list.get(0).getId());
        c.follow(list.get(1).getId());
        c.own(list2.get(0).getId());
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
        System.out.println("populated");
    }*/
    
}
