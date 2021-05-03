package com.cymetrics.persistence.rdbms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="dev", schema="dev")
@Entity(name = "dev")
public class CountDo implements Serializable {
    public void setId(int id) {
        this.id = id;
    }

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "count")
    private int count;

    public void inc(){
        this.count++;
    }

    public int getCount(){
        return this.count;
    }

    public int getId() {
        return this.count;
    }
}
