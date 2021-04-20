package hk.onedegree.persistence.rdbms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="USER")
@Entity(name = "user")
public class UserDo implements Serializable {
    public UserDo(){}

    @Id
    @Column(name = "ID")
    private String id;

    public UserDo(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    @Column(name = "EMAIL", unique=true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}