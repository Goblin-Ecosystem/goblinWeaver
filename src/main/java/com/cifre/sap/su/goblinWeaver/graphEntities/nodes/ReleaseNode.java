package com.cifre.sap.su.goblinWeaver.graphEntities.nodes;

import org.json.simple.JSONObject;

public class ReleaseNode extends NodeObject {
    private final long timestamp;
    private final String version;

    public ReleaseNode(String neo4jId, String id, long timestamp, String version) {
        super(neo4jId, id, NodeType.RELEASE);
        this.timestamp = timestamp;
        this.version = version;
    }

    public String getGa() {
        return getId().equals("ROOT") ? "ROOT" : getId().substring(0, getId().lastIndexOf(':'));
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = super.getJsonObject();
        jsonObject.put("version", version);
        jsonObject.put("timestamp", timestamp);
        return jsonObject;
    }
}
