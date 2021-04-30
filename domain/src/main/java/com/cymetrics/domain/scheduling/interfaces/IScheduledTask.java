package com.cymetrics.domain.scheduling.interfaces;
import org.json.JSONObject;

public interface IScheduledTask {
    public void run(JSONObject args);
}
