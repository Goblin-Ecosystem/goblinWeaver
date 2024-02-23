package com.cifre.sap.su.goblinWeaver.graphEntities.nodes;

import org.json.simple.JSONObject;

public class ArtifactNode extends NodeObject {
    private final boolean found;

    public ArtifactNode(String neo4jId, String id, boolean found) {
        super(neo4jId, id, NodeType.ARTIFACT);
        this.found = found;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = super.getJsonObject();
        jsonObject.put("found", found);
        return jsonObject;
    }
}
