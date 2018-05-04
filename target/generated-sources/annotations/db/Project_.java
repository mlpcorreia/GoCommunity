package db;

import java.sql.Date;
import java.util.HashMap;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-04T15:05:54")
@StaticMetamodel(Project.class)
public class Project_ { 

    public static volatile SingularAttribute<Project, Date> endsOn;
    public static volatile SingularAttribute<Project, Double> goal;
    public static volatile SingularAttribute<Project, String> name;
    public static volatile SingularAttribute<Project, String> description;
    public static volatile SingularAttribute<Project, Double> progress;
    public static volatile SingularAttribute<Project, Long> id;
    public static volatile SingularAttribute<Project, HashMap> milestones;
    public static volatile SingularAttribute<Project, Date> createdOn;

}