package db;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 * Project Entity
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
    private Long owner;
    private ArrayList<Long> followers;
    @OneToMany(mappedBy = "project")
    private List<Comment> comments;
    
    public Project() {
        
    }
    
    public Project(String name, String desc, Double goal, Date ends, Long owner) {
        this.name = name;
        this.description = desc;
        this.goal = goal;
        this.createdOn = new Date(Calendar.getInstance().getTime().getTime());
        this.endsOn = ends;
        this.milestones = new HashMap<>();
        this.progress = 0.0;
        this.owner = owner;
        this.followers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }
    
    public String getOverview() {
        StringBuilder res = new StringBuilder();
        
        res.append(name);
        res.append(String.format(" - %.2f€/%.2f€ - Ends on ",progress,goal));
        res.append(endsOn.toString());
        
        return res.toString();
    }
    
    public String printFormattedProgress() {
        return String.format("%.2f€",progress);
    }
    
    public String printFormattedGoal() {
        return String.format("%.2f€",goal);
    }
    
    public void initValues() {
       this.milestones = new HashMap<>();
       this.progress = 0.0;
       this.followers = new ArrayList<>();
       this.comments = new ArrayList<>(); 
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOwner() {
        return owner;
    }
    
    public void setOwner(Long owner) {
        this.owner=owner;
    }
    
    public void followedBy(long i) {
        if (!followers.contains(i)) {
            followers.add(i);
        }
    }
    
    public void unfollowedBy(long i) {
        followers.remove(i);
    }
    
    public List<Long> getFollowers() {
        return followers;
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
        double key = Math.round(money*100d)/100d;
        milestones.put(key, text);
    }
    
    public List<Double> getMilestoneKeys() {
        return new ArrayList<>(milestones.keySet());
    }
    
    public String getMilestoneText(double key) {
        return milestones.get(key);
    }
    
    public int getAmountOfMilestones() {
        return milestones.keySet().size();
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
    
    public void addComment(Comment c) {
        comments.add(c);
    }
    
    public List<Comment> getComments() {
        return comments;
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
