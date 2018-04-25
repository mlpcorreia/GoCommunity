package db;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author Carlos
 */
@Entity
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private HashMap<Double,String> milestones;
    private Double goal;
    private Double progress;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createdOn;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endsOn;
    
    public Project() {
        
    }
    
    public Project(String name, String desc, Double goal, Date created, Date ends) {
        this.name = name;
        this.description = desc;
        this.goal = goal;
        this.createdOn = created;
        this.endsOn = ends;
        this.milestones = new HashMap<>();
        this.progress = 0.0;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addMilestone(Double money, String text) {
        milestones.put(money, text);
    }
    
    public Map<Double,String> getMilestones() {
        return milestones;
    }
    
    public Double getGoal() {
        return goal;
    }
    
    public void setGoal(Double goal) {
        this.goal = goal;
    }
    
    public Double getProgress() {
        return progress;
    }
    public void addToProgress(Double money) {
        progress += money;
    }
    
    public void setCreatedOn(Date date) {
        this.createdOn = date;
    }
    
    public Date getCreatedOn() {
        return createdOn;
    }
    
    public void setEndsOn(Date date) {
        this.endsOn = date;
    }
    
    public Date getEndsOn() {
        return endsOn;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "db.Project[ id=" + id + " ]";
    }
    
}
