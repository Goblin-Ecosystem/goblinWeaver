package com.cifre.sap.su.goblinWeaver.graphEntities.edges;

import com.cifre.sap.su.goblinWeaver.graphEntities.GraphObject;
import org.json.simple.JSONObject;

import java.util.Objects;

public abstract class EdgeObject implements GraphObject {
    private String sourceId;
    private String targetId;
    private final EdgeType type;

    public EdgeObject(String sourceId, String targetId, EdgeType type) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public EdgeType getType() {
        return type;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sourceId",sourceId);
        jsonObject.put("targetId",targetId);
        jsonObject.put("type",type);
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeObject that = (EdgeObject) o;
        return sourceId.equals(that.sourceId) && targetId.equals(that.targetId) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, targetId, type);
    }
}
