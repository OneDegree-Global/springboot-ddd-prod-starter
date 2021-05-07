package com.cymetrics.persistence.rdbms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Table(name="schedule", schema="public")
@Entity(name = "schedule")
public class ScheduleDo implements Serializable {
    public ScheduleDo(){}

    @Id
    @Column(name = "name", unique=true, nullable = false)
    private String name;

    @Column(name = "command", unique=true, nullable = false)
    private String command;

    @Column(name = "isActive")
    private Boolean isActive = true;


    @Column(name = "isOverwrite")
    private Boolean isReProducible = false;

    @Column(name = "effectiveTime")
    private Timestamp effectiveTime = null;

    @Column(name = "args")
    private String args = "";

    @Column(name = "args")
    private String cronExpression = "";

    public ScheduleDo(String name, String command, Boolean isActive, Boolean isReProducible, Timestamp effectiveTime, String args, String cronExpression) {
        this.name = name;
        this.command = command;
        this.isActive = isActive;
        this.isReProducible = isReProducible;
        this.effectiveTime = effectiveTime;
        this.args = args;
        this.cronExpression = cronExpression;
    }


    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getIsReProducible() {
        return isReProducible;
    }

    public Timestamp getEffectiveTime() {
        return effectiveTime;
    }

    public String getArgs() {
        return args;
    }

    public String getCronExpression() {
        return cronExpression;
    }
}
