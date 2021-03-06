package com.cymetrics.domain.messaging.types;

import com.cymetrics.domain.auth.services.UserAuthInfoService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

public class JsonMessage implements Serializable{

    private String value;
    private static Logger logger = LoggerFactory.getLogger(UserAuthInfoService.class);

    public JsonMessage(JSONObject json){
        value = json.toString();
    }

    public JSONObject getJSON() {
        try {
            return new JSONObject(value);
        } catch(JSONException e){
            logger.error("get JSONObject from JSONMessage exception:"+e.toString());
            return new JSONObject();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonMessage that = (JsonMessage) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}