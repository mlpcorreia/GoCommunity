package db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Carlos
 */
@Entity
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    private ArrayList<Long> follows;
    private ArrayList<Long> owns;
    
    public Client() {
        
    }
    
    public Client(String name, String user, String pw) {
        this.name = name;
        this.username = user;
        this.password = pw;
        this.follows = new ArrayList<>();
        this.owns = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void follow(long i) {
        follows.add(i);
    }
    
    public List<Long> getFollows() {
        return follows;
    }
    
    public void own(long i) {
        owns.add(i);
    }
    
    public List<Long> getOwns() {
        return owns;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Client)) {
            return false;
        }
        
        Client other = (Client) object;
        
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "db.Client[ id=" + id + " ]";
    }
    
}
