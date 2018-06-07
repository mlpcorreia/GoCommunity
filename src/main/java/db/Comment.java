package db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 * Comment Entity
 */
@Entity
public class Comment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userVisibleName;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date creationTime;
    private String content;
    @ManyToOne
    private Project project;

    public Comment() {
        
    }
    
    public Comment(Client c, String content) {
        userVisibleName = c.getName();
        creationTime = Calendar.getInstance().getTime();
        this.content = content;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public void setDate(Date d) {
        creationTime = d;
    }
    
    public void setUserVisibleName(String userVisibleName) {
        this.userVisibleName = userVisibleName;
    }
    
    public String getUserVisibleName() {
        return userVisibleName;
    }
    
    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy '-' h:mm a");
        return format.format(creationTime);
    }
    
    public String getApiFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy");
        return format.format(creationTime);
    }
    
    public Date getDate() {
        return creationTime;
    }
    
    public String getHeader() {
        return "Posted by "+userVisibleName;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
}
