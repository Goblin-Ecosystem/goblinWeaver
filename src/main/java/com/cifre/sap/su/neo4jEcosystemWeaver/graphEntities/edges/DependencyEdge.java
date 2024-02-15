package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges;

import org.json.simple.JSONObject;

public class DependencyEdge extends EdgeObject {
    private final String targetVersion;
    private final String scope;

    public DependencyEdge(String sourceId, String targetId, String targetVersion, String scope) {
        super(sourceId, targetId, EdgeType.DEPENDENCY);
        this.targetVersion = targetVersion;
        this.scope = scope;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = super.getJsonObject();
        jsonObject.put("targetVersion", targetVersion);
        jsonObject.put("scope", scope);
        return jsonObject;
    }
}
