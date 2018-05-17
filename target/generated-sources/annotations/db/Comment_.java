package db;

import db.Project;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-17T15:49:38")
@StaticMetamodel(Comment.class)
public class Comment_ { 

    public static volatile SingularAttribute<Comment, Date> creationTime;
    public static volatile SingularAttribute<Comment, String> userVisibleName;
    public static volatile SingularAttribute<Comment, Project> project;
    public static volatile SingularAttribute<Comment, Long> id;
    public static volatile SingularAttribute<Comment, String> content;
    public static volatile SingularAttribute<Comment, String> username;

}