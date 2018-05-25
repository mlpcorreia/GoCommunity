package com.mycompany.gocommunity;

import java.util.logging.Logger;
import db.Client;
import db.Comment;
import db.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    private static final String PROJECTNAMEQUERY = "SELECT u FROM Project u WHERE u.name=:name";
    private static final String PROJECTIDQUERY = "SELECT u FROM Project u WHERE u.id=:id";
    private static final String CLIENTNAMEQUERY = "SELECT u FROM Client u WHERE u.username=:user";
    private static final String CLIENTIDQUERY = "SELECT u FROM Client u WHERE u.id=:id";
    
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
    
    public int tryLogin(Client c, String word) {
        Client aux = em.find(Client.class, c.getId());
        if (aux.canLogIn()) { //can login
            em.getTransaction().begin();
            if (word.equals(aux.getPassword())) { //can login, valid
                aux.goodLogin();
                em.getTransaction().commit();
                return 0;
            } else { //can login, failed
                aux.badLogin();
                em.getTransaction().commit();
                return 1;
            }
        } else { //cant login
            return 2;
        }
    }
    
    public List<Project> searchProjects(String search) {
        TypedQuery<Project> query = em.createQuery(
            "SELECT u FROM Project u WHERE u.name LIKE :n", Project.class);
        
        List<Project> res = query.setParameter("n", "%"+search+"%").getResultList();
        if (!res.isEmpty()) {
            return res;
        } else return new ArrayList<>();
    }
    
    public Client login(String username, String word) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username=:user AND u.word=:word", Client.class);
        
        List<Client> res = query.setParameter("user", username).setParameter("word", word).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Client apiGetUser(long id) {
        TypedQuery<Client> query = em.createQuery(
                CLIENTIDQUERY, Client.class);

        List<Client> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Client apiGetUser(String username) {
        TypedQuery<Client> query = em.createQuery(
            CLIENTNAMEQUERY, Client.class);
        
        List<Client> res = query.setParameter("user", username).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project apiGetProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                PROJECTIDQUERY, Project.class);
        
        List<Project> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project apiGetProject(String name) {
        TypedQuery<Project> query = em.createQuery(
                PROJECTNAMEQUERY, Project.class);
        
        List<Project> res = query.setParameter("name", name).getResultList();

        if (!res.isEmpty()) {
            return res.get(0);
        } else return null;
    }
    
    public Project getProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                PROJECTIDQUERY, Project.class);
        
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
                CLIENTNAMEQUERY, Client.class);
        
        if (!query.setParameter("user", user.getUsername()).getResultList().isEmpty()) return false;
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return true;
    }
    
    public long createProject(Project p) {
        TypedQuery<Project> query = em.createQuery(
               PROJECTNAMEQUERY, Project.class);

        if (!query.setParameter("name", p.getName()).getResultList().isEmpty()) return -1;
        
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        
        TypedQuery<Project> laterQuery = em.createQuery(
                PROJECTNAMEQUERY, Project.class);
        
        return laterQuery.setParameter("name", p.getName()).getSingleResult().getId();
    }
    
    public void updateComment(Project p, Comment c) {
        Project auxp = em.find(Project.class, p.getId());
        em.getTransaction().begin();
        c.setProject(auxp);
        em.persist(c);
        auxp.addComment(c);       
        em.getTransaction().commit();
    }
    
    public void clearDatabase() {
        em.getTransaction().begin();
        int x = em.createQuery("DELETE FROM Client").executeUpdate();
        int y = em.createQuery("DELETE FROM Project").executeUpdate();
        em.getTransaction().commit();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Deleted ").append(x).append(" clients, ").append(y).append(" projects.");
        
        Logger.getLogger("GoCommunityLog").log(Level.INFO, sb.toString());
    }   
}
