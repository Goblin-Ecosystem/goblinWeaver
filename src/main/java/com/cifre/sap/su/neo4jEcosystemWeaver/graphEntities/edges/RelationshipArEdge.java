package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges;

import org.json.simple.JSONObject;

public class RelationshipArEdge extends EdgeObject{

    public RelationshipArEdge(String sourceId, String targetId) {
        super(sourceId, targetId, EdgeType.RELATIONSHIP_AR);
    }

    @Override
    public JSONObject getJsonObject() {
        return super.getJsonObject();
    }
}
