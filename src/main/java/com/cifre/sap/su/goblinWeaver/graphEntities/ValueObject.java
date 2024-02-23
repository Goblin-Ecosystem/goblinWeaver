package com.cifre.sap.su.goblinWeaver.graphEntities;

import org.json.simple.JSONObject;

import java.util.Collections;

public class ValueObject implements GraphObject {
    private final String key;
    private final String value;

    public ValueObject(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public JSONObject getJsonObject() {
        return new JSONObject(Collections.singletonMap(key, value));
    }
}
