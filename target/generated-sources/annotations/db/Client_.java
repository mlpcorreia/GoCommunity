package db;

import java.util.ArrayList;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-04T15:05:54")
@StaticMetamodel(Client.class)
public class Client_ { 

    public static volatile SingularAttribute<Client, String> password;
    public static volatile SingularAttribute<Client, String> name;
    public static volatile SingularAttribute<Client, ArrayList> follows;
    public static volatile SingularAttribute<Client, ArrayList> owns;
    public static volatile SingularAttribute<Client, Long> id;
    public static volatile SingularAttribute<Client, String> username;

}