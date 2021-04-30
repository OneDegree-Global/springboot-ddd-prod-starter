package com.cymetrics.messaging.messageTypes;

import org.json.JSONObject;

import java.io.Serializable;

public class JSONMessage implements Serializable{
    public String value;
    public JSONMessage(JSONObject json){
        value = json.toString();
    }

    public JSONObject getJSON(){
        return new JSONObject(value);
    }

}