package com.mycompany.gocommunity;

import com.sun.media.jfxmedia.logging.Logger;
import db.Client;
import db.Project;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.JDOHelper;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Carlos
 */
public class DatabaseHandler {
    
    private final EntityManager em;
    private final String basicProjectNameQuery = "SELECT u FROM Project u WHERE u.name=:name";
    private final String basicProjectIdQuery = "SELECT u FROM Project u WHERE u.id=:id";
    private final String basicClientNameQuery = "SELECT u FROM Client u WHERE u.username=:user";
    private final String basicClientIdQuery = "SELECT u FROM Client u WHERE u.id=:id";
    
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
    
    public List<Project> searchProjects(String search) {
        TypedQuery<Project> query = em.createQuery(
            "SELECT u FROM Project u WHERE u.name LIKE :n", Project.class);
        
        List<Project> res = query.setParameter("n", "%"+search+"%").getResultList();
        if (!res.isEmpty()) {
            return res;
        } else return new ArrayList<>();
    }
    
    public Client login(String username, String password) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username=:user AND u.password=:pass", Client.class);
        
        List<Client> res = query.setParameter("user", username).setParameter("pass", password).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Client apiGetUser(long id) {
        TypedQuery<Client> query = em.createQuery(
                basicClientIdQuery, Client.class);

        List<Client> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Client apiGetUser(String username) {
        TypedQuery<Client> query = em.createQuery(
            basicClientNameQuery, Client.class);
        
        List<Client> res = query.setParameter("user", username).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project apiGetProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                basicProjectIdQuery, Project.class);
        
        List<Project> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project apiGetProject(String name) {
        TypedQuery<Project> query = em.createQuery(
                basicProjectNameQuery, Project.class);
        
        List<Project> res = query.setParameter("name", name).getResultList();

        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project getProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                basicProjectIdQuery, Project.class);
        
        return query.setParameter("id", id).getSingleResult();
    }
    
    public List<Project> getPopularProjects() {
        TypedQuery<Project> query = em.createQuery(
                "SELECT u FROM Project u ORDER BY u.progress DESC", 
                Project.class).setMaxResults(10);
        
        return query.getResultList();
    }
    
    public boolean createAccount(Client user) {
        TypedQuery<Client> query = em.createQuery(
                basicClientNameQuery, Client.class);
        
        if (query.setParameter("user", user.getUsername()).getResultList().size()>0) return false;
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return true;
    }
    
    public long createProject(Project p) {
        TypedQuery<Project> query = em.createQuery(
               basicProjectNameQuery, Project.class);

        if (query.setParameter("name", p.getName()).getResultList().size()>0) return -1;
        
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        
        TypedQuery<Project> laterQuery = em.createQuery(
                basicProjectNameQuery, Project.class);
        
        return laterQuery.setParameter("name", p.getName()).getSingleResult().getId();
    }
    
    public void clearDatabase() {
        em.getTransaction().begin();
        int x = em.createQuery("DELETE FROM Client").executeUpdate();
        int y = em.createQuery("DELETE FROM Project").executeUpdate();
        em.getTransaction().commit();
        
        Logger.logMsg(Logger.INFO, "Deleted "+x+" clients, "+y+" projects.");
    }
}
