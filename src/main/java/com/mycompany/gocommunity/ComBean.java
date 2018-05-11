package com.mycompany.gocommunity;

import db.Client;
import db.Project;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Carlos
 */

@ManagedBean
@ApplicationScoped
public class ComBean {     
    
    private Client user;
    private Project activeProject;
    
    private String donation;
    private String milestoneKey;
    private String milestoneText;
    
    private String name;
    private String username;
    private String password;
    
    private String projName;
    private String projDesc;
    private String projGoalString;
    private String projEndsString;
    
    private final DatabaseHandler db;  
    private String loginErrorMessage;
    private String createAccountErrorMessage;
    private String createProjectErrorMessage;
    private String donationErrorMessage;
    private String createMilestoneErrorMessage;
    
    public ComBean() {
        db = new DatabaseHandler("go.odb");
        loginErrorMessage = "";
        createAccountErrorMessage = "";
        createProjectErrorMessage = "";
        donationErrorMessage = "";
        createMilestoneErrorMessage = "";
    }
    
    public ComBean(String dbfile) {
        db = new DatabaseHandler(dbfile);
        loginErrorMessage = "";
        createAccountErrorMessage = "";
        createProjectErrorMessage = "";
        donationErrorMessage = "";
        createMilestoneErrorMessage = "";
    }
    
    public String login() {
        this.user = db.login(username,password);
        
        if (user!=null) {
            loginErrorMessage = "";
            return "main.xhtml";
        } else {
            loginErrorMessage = "Username+password combination not found. Please retry.";
            return "login.xhtml";
        }
    }
    
    public String createAccount() {
        //fix bad characters?
        //encrypt pword
        if (name==null || username==null || password==null || 
                name.equals("") || username.equals("") || password.equals("")) {
            createAccountErrorMessage = "Every field is required.";
            return "newAccount.xhtml";
        }
        
        Client newUser = new Client(name, username, password);
        
        if (db.createAccount(newUser)) {
            createAccountErrorMessage = "";
            this.user = newUser;
            return "main.xhtml";
        } else {
            createAccountErrorMessage = "This username already exists.";
            return "newAccount.xhtml";
        }
    }
    
    public String createProject() {
        //do regex stuff
        //date is yyyy-mm-dd
        
        if (projName==null || projDesc==null || projGoalString==null ||
                projEndsString==null || projName.equals("") || projDesc.equals("") ||
                projGoalString.equals("") || projEndsString.equals("")) {
            createProjectErrorMessage = "Every field is required.";
            return "newProject.xhtml";
        }
        
        double goal;
        Date end;
        
        try {
            goal = Double.parseDouble(projGoalString);
            end = Date.valueOf(projEndsString);
        } catch (NumberFormatException e) {
            createProjectErrorMessage = "Please insert a valid number in the \"goal\" field.";
            return "newProject.xhtml";
        } catch (IllegalArgumentException e) {
            createProjectErrorMessage = "Please respect the date syntax.";
            return "newProject.xhtml";
        }
        
        if (end.before(new Date(Calendar.getInstance().getTime().getTime()))) {
            createProjectErrorMessage = "Expiration date cannot be earlier than creation date.";
            return "newProject.xhtml";
        }
        
        Project newProject = new Project(projName, projDesc, goal, end, user.getId());
        long id = db.createProject(newProject);
        
        if (id!=-1) {
            createProjectErrorMessage = "";
            user.own(id);
            db.updateField(user, "own");
            return "main.xhtml";
        } else {
            createProjectErrorMessage = "A project with this name already exists.";
            return "newProject.xhtml";
        }

    }
    
    public void donate() {
        if (donation==null || donation.equals("")) {
            donationErrorMessage = "Please insert a monetary value.";
            return;
        }
        
        double amt;
        
        try {
            amt = Double.parseDouble(donation);
        } catch (NumberFormatException e) {
            donationErrorMessage = "Please insert a valid number in the \"donation\" field.";
            return;
        }
        
        db.updateProgress(activeProject, amt);
        donationErrorMessage = "";
    }
    
    public void addMilestone() {
        if (milestoneKey==null || milestoneKey.equals("") ||
                milestoneText==null || milestoneText.equals("")) {
            createMilestoneErrorMessage = "Both fields are required.";
            return;
        }
        
        double key;
        
        try {
            key = Double.parseDouble(milestoneKey);
        } catch (NumberFormatException e) {
            createMilestoneErrorMessage = "Please insert a valid number in the \"value\" field.";
            return;
        }
        
        activeProject.addMilestone(key, milestoneText);
        db.updateField(activeProject, "milestones");
        createMilestoneErrorMessage = "";
    }
    
    public void follow() {
        user.follow(activeProject.getId());
        db.updateField(user, "follow");
        activeProject.followedBy(user.getId());
        db.updateField(activeProject, "followers");
    }
    
    public void unfollow() {
        user.unfollow(activeProject.getId());
        db.updateField(user, "follow");
        activeProject.unfollowedBy(user.getId());
        db.updateField(activeProject, "followers");
    }
    
    public boolean isVisitingFollowedProject() {
        return user.getFollows().contains(activeProject.getId());
    }
    
    public boolean isVisitingOwnedProject() {
        return user.getOwns().contains(activeProject.getId());
    }
    
    public String goToOwnedProjectPage(byte id) {
        activeProject = db.getProject(user.getOwns().get(id));
        return "project.xhtml";
    }
    
    public String goToFollowedProjectPage(byte id) {
        activeProject = db.getProject(user.getFollows().get(id));
        return "project.xhtml";
    }
    
    public String goToProjectPage(byte id) {
        activeProject = db.getPopularProjects().get(id);
        return "project.xhtml";
    }

    public Project getProject(int id) {
        return db.getProject(id);
    }
    
    public List<Project> getPopularProjects() {
        return db.getPopularProjects();
    }
    
    public Project getActiveProject() {
        return activeProject;
    }
    
    public Client getUser() {
        return user;
    }
    
    public void setDonation(String donation) {
        this.donation = donation;
    }
    
    public String getDonation() {
        return donation;
    }
    
    public void setProjName(String projName) {
        this.projName = projName;
    }
    
    public String getProjName() {
        return projName;
    }
    
    public String getMilestoneKey() {
        return milestoneKey;
    }
    
    public String getMilestoneText() {
        return milestoneText;
    }
    
    public void setMilestoneKey(String milestoneKey) {
        this.milestoneKey = milestoneKey;
    }
    
    public void setMilestoneText(String milestoneText) {
        this.milestoneText = milestoneText;
    }
    
    public void setProjDesc(String projDesc) {
        this.projDesc = projDesc;
    }
    
    public String getProjDesc() {
        return projDesc;
    }
    
    public void setProjGoalString(String projGoalString) {
        this.projGoalString = projGoalString;
    }
    
    public String getProjGoalString() {
        return projGoalString;
    }
    
    public void setProjEndsString(String projEndsString) {
        this.projEndsString = projEndsString;
    }
    
    public String getProjEndsString() {
        return projEndsString;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCreateAccountErrorMessage() {
        return createAccountErrorMessage;
    }
    
    public String getCreateMilestoneErrorMessage() {
        return createMilestoneErrorMessage;
    }
    
    public String getCreateProjectErrorMessage() {
        return createProjectErrorMessage;
    }
    
    public String getDonationErrorMessage() {
        return donationErrorMessage;
    }
    
    public String getLoginErrorMessage() {
        return loginErrorMessage;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
}
