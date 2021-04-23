package com.cymetrics.messaging.messageTypes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.json.JSONObject;

public class JSONMessage implements Serializable{
    public String value;
    public JSONMessage(JSONObject json){
        value = json.toString();
    }

    public JSONObject getJSON(){
        return new JSONObject(value);
    }

}