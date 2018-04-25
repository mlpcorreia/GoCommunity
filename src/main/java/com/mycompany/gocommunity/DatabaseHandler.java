package com.mycompany.gocommunity;

import db.Client;
import db.Project;
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
    
    public DatabaseHandler(String file) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/"+file);
        this.em = emf.createEntityManager();
    }
    
    public Client login(String username, String password) {
        TypedQuery<Client> query = em.createQuery(
            "SELECT u FROM Client u WHERE u.username='"+username+
                    "' AND u.password='"+password+"'", Client.class);
        if (query.getResultList().size()>0) {
            return query.getResultList().get(0);
        } else return null;
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
}
