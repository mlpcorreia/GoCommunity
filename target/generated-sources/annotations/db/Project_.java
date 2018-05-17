package db;

import db.Comment;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-17T15:49:38")
@StaticMetamodel(Project.class)
public class Project_ { 

    public static volatile SingularAttribute<Project, Long> owner;
    public static volatile SingularAttribute<Project, Date> endsOn;
    public static volatile SingularAttribute<Project, Double> goal;
    public static volatile SingularAttribute<Project, ArrayList> followers;
    public static volatile ListAttribute<Project, Comment> comments;
    public static volatile SingularAttribute<Project, String> name;
    public static volatile SingularAttribute<Project, String> description;
    public static volatile SingularAttribute<Project, Double> progress;
    public static volatile SingularAttribute<Project, Long> id;
    public static volatile SingularAttribute<Project, HashMap> milestones;
    public static volatile SingularAttribute<Project, Date> createdOn;

}