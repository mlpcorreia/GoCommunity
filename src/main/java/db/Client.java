package db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 * Client Entity
 */
@Entity
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String word;
    private ArrayList<Long> follows;
    private ArrayList<Long> owns;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastBadLogin;
    private int loginTries;
    
    public Client() {
        
    }
    
    public Client(String name, String user, String pw) {
        this.name = name;
        this.username = user;
        this.word = pw;
        this.follows = new ArrayList<>();
        this.owns = new ArrayList<>();
        this.lastBadLogin = Calendar.getInstance().getTime();
        this.loginTries = 3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setLastBadLogin(long ms) {
        this.lastBadLogin = new Date(ms);
    }
    
    public void setLoginTries(int i) {
        this.loginTries = i;
    }
    
    public void goodLogin() {
        loginTries = 3;
    }
    
    public void badLogin() {
        loginTries -= 1;
        this.lastBadLogin = Calendar.getInstance().getTime();
    }
    
    public boolean canLogIn() {
        Date now = Calendar.getInstance().getTime();
        if (loginTries>0) {
            return true;
        } else if (now.getTime()-lastBadLogin.getTime()>=(24*60*60*1000)) {
            //24h lock is over
            loginTries = 3;
            return true;
        } else return false;
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
        return word;
    }
    
    public void setPassword(String password) {
        this.word = password;
    }
    
    public void follow(long i) {
        if (!follows.contains(i))
            follows.add(i);
    }
    
    public void unfollow(long i) {
        follows.remove(i);
    }
    
    public List<Long> getFollows() {
        return follows;
    }
    
    public void own(long i) {
        if (!owns.contains(i))
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
