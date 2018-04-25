package com.mycompany.gocommunity;

import db.Client;
import db.Project;
import java.sql.Date;
import java.util.Calendar;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Carlos
 */

@ManagedBean
@ApplicationScoped
public class ComBean {
       
    private String name;
    private String username;
    private String password;
    private Client user;
    private long activeProject;
    
    private String projName;
    private String projDesc;
    private String projGoalString;
    private String projEndsString;
    
    private final DatabaseHandler db;  
    private String loginErrorMessage;
    private String createAccountErrorMessage;
    private String createProjectErrorMessage;
    
    public ComBean() {
        db = new DatabaseHandler("go.odb");
        loginErrorMessage = "";
        createAccountErrorMessage = "";
        createProjectErrorMessage = "";
    }
    
    public String login() {
        //username: x
        //password: d
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
        Date now = new Date(Calendar.getInstance().getTime().getTime());
        
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
        
        Project newProject = new Project(projName, projDesc, goal, now, end);
        long id = db.createProject(newProject);
        
        if (id!=-1) {
            createProjectErrorMessage = "";
            user.own(id);
            return "main.xhtml";
        } else {
            createProjectErrorMessage = "A project with this name already exists.";
            return "newAccount.xhtml";
        }

    }
    
    public Client getUser() {
        return user;
    }
    
    public void setProjName(String projName) {
        this.projName = projName;
    }
    
    public String getProjName() {
        return projName;
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
    
    public String getCreateProjectErrorMessage() {
        return createProjectErrorMessage;
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
