package com.mycompany.gocommunity;

import db.Client;
import db.Project;
import java.util.List;
import javax.jdo.JDOHelper;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Carlos
 */
public class DatabaseHandler {
    
    private final EntityManager em;
    
    public DatabaseHandler(String file) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/"+file);
        this.em = emf.createEntityManager();
    }
    
    public void updateField(Object entity, String field) {
        em.getTransaction().begin();
        JDOHelper.makeDirty(entity, field);
        em.getTransaction().commit();
    }
    
    public void updateProgress(Project p, double amt) {
        Project aux = em.find(Project.class, p.getId());
        em.getTransaction().begin();
        aux.addToProgress(amt);
        em.getTransaction().commit();
    }
    
    public Client login(String username, String password) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username='"+username+
                    "' AND u.password='"+password+"'", Client.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
    }
    
    public Client apiGetUser(long id) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.id="+id, Client.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
    }
    
    public Client apiGetUser(String username) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username='"+username+"'", Client.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
    }
    
    public Project apiGetProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u WHERE u.id="+id, Project.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
    }
    
    public Project apiGetProject(String name) {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u WHERE u.name='"+name+"'", Project.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
    }
    
    public Project getProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u WHERE u.id="+id, Project.class);
        
        return query.getSingleResult();
    }
    
    public List<Project> getPopularProjects() {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u ORDER BY u.progress DESC", 
                Project.class).setMaxResults(10);
        
        return query.getResultList();
    }
    
    public boolean createAccount(Client user) {
        TypedQuery<Client> query = em.createQuery(
                "SELECT u FROM Client u WHERE u.username='"+user.getUsername()+
                        "'", Client.class);
        
        if (query.getResultList().size()>0) return false;
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return true;
    }
    
    public long createProject(Project p) {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u WHERE u.name='"+p.getName()+
                        "'", Project.class);
        
        if (query.getResultList().size()>0) return -1;
        
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        
        TypedQuery<Project> laterQuery = em.createQuery(
                "SELECT u FROM Project u WHERE u.name='"+p.getName()+
                        "'", Project.class);
        
        return laterQuery.getSingleResult().getId();
    }
    
    public void clearDatabase() {
        em.getTransaction().begin();
        int x = em.createQuery("DELETE FROM Client").executeUpdate();
        int y = em.createQuery("DELETE FROM Project").executeUpdate();
        em.getTransaction().commit();
        
        System.out.println("Deleted "+x+" clients, "+y+" projects.");
    }
}
