package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities;

import org.json.simple.JSONObject;

import java.util.Collections;

public class ValueObject implements GraphObject {
    private String key;
    private String value;

    public ValueObject(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public JSONObject getJsonObject() {
        return new JSONObject(Collections.singletonMap(key, value));
    }

    @Override
    public String toString() {
        return "key='" + key + '\'' +
                ", value='" + value + '\'';
    }
}
