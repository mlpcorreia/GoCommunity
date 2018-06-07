package com.mycompany.gocommunity;

import db.Client;
import db.Comment;
import db.Project;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.JDOHelper;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Manage the database
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
        } else {
            return new ArrayList<>();
        }
    }
    
    public Client login(String username, String word) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username=:user AND u.word=:word", Client.class);
        
        List<Client> res = query.setParameter("user", username).setParameter("word", word).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else {
            return null;
        }
    }
    
    public int apiChangeStance(long uid, long pid) {
        Client c = em.find(Client.class, uid);
        if (c==null) {
            return -1;
        }
        Project p = em.find(Project.class, pid);
        if (p==null) {
            return -2;
        }
        if (c.getOwns().contains(pid)) {
            return -3;
        }
        
        if (c.getFollows().contains(pid)) { //unfollow
            c.unfollow(pid);
            updateField(c, "follow");
            p.unfollowedBy(uid);
            updateField(p, "followers");
            return 0;
        } else { //follow
            c.follow(pid);
            updateField(c, "follow");
            p.followedBy(uid);
            updateField(p, "followers");
            return 1;
        }
    }
    
    public Client apiGetUser(long id) {
        TypedQuery<Client> query = em.createQuery(
                CLIENTIDQUERY, Client.class);

        List<Client> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else {
            return null;
        }
    }
    
    public Client apiGetUser(String username) {
        TypedQuery<Client> query = em.createQuery(
            CLIENTNAMEQUERY, Client.class);
        
        List<Client> res = query.setParameter("user", username).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else {
            return null;
        }
    }
    
    public Project apiGetProject(long id) {
        TypedQuery<Project> query = em.createQuery(
                PROJECTIDQUERY, Project.class);
        
        List<Project> res = query.setParameter("id", id).getResultList();
        if (!res.isEmpty()) {
            return res.get(0);
        } else {
            return null;
        }
    }
    
    public Project apiGetProject(String name) {
        TypedQuery<Project> query = em.createQuery(
                PROJECTNAMEQUERY, Project.class);
        
        List<Project> res = query.setParameter("name", name).getResultList();

        if (!res.isEmpty()) {
            return res.get(0);
        } else {
            return null;
        }
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
        
        if (!query.setParameter("user", user.getUsername()).getResultList().isEmpty()) {
            return false;
        }
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return true;
    }
    
    public long apiCreateAccount(Client user) {
        TypedQuery<Client> query = em.createQuery(
                CLIENTNAMEQUERY, Client.class);
        
        if (!query.setParameter("user", user.getUsername()).getResultList().isEmpty()) {
            return -1;
        }
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        
        TypedQuery<Client> laterQuery = em.createQuery(
                CLIENTNAMEQUERY, Client.class);
        
        return laterQuery.setParameter("user", user.getUsername()).getSingleResult().getId();
    }
    
    public boolean apiDonate(double amt, long pid) {
        Project aux = em.find(Project.class, pid);
        if (aux==null) {
            return false;
        }
        
        em.getTransaction().begin();
        aux.addToProgress(amt);
        em.getTransaction().commit();
        
        return true;
    }
    
    public boolean apiAddMilestone(double amt, String desc, long pid) {
        Project aux = em.find(Project.class, pid);
        if (aux==null) {
            return false;
        }
        
        aux.addMilestone(amt, desc);
        updateField(aux, "milestones");
        return true;
    }
    
    public int apiAddComment(long uid, String content, long pid) {
        Client auxc = em.find(Client.class, uid);
        if (auxc==null) {
            return -1;
        }
        
        Project auxp = em.find(Project.class, pid);
        if (auxp==null) {
            return -2;
        }
        
        Comment c = new Comment(auxc, content);
        
        em.getTransaction().begin();
        c.setProject(auxp);
        em.persist(c);
        auxp.addComment(c);       
        em.getTransaction().commit();
        return 0;
    }
    
    public long apiCreateProject(Project p) {
        TypedQuery<Project> query = em.createQuery(
               PROJECTNAMEQUERY, Project.class);
        
        if (!query.setParameter("name", p.getName()).getResultList().isEmpty()){
            return -1;
        }
        
        Client c = em.find(Client.class, p.getOwner());
        if (c==null) {
            return -2;
        }
        
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        
        TypedQuery<Project> laterQuery = em.createQuery(
                PROJECTNAMEQUERY, Project.class);
        
        long pid = laterQuery.setParameter("name", p.getName()).getSingleResult().getId();
        
        c.own(pid);
        updateField(c, "own");
        
        return pid;
    }
    
    public long createProject(Project p) {
        TypedQuery<Project> query = em.createQuery(
               PROJECTNAMEQUERY, Project.class);

        if (!query.setParameter("name", p.getName()).getResultList().isEmpty()) {
            return -1;
        }
        
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
}
