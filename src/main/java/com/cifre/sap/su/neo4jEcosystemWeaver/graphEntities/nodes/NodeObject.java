package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.GraphObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValue;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class NodeObject implements GraphObject {
    private final String neo4jId;
    private final String id;
    private final NodeType type;
    private final Set<AddedValue> addedValues = new HashSet<>();

    public NodeObject(String neo4jId, String id, NodeType type) {
        this.neo4jId = neo4jId;
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public String getNeo4jId() {
        return neo4jId;
    }

    public void addAddedValue(AddedValue addedValue){
        this.addedValues.add(addedValue);
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",id);
        jsonObject.put("nodeType",type);
        for(AddedValue addedValue : addedValues){
            jsonObject.putAll(addedValue.getValueMap());
        }
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeObject that = (NodeObject) o;
        return neo4jId.equals(that.neo4jId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neo4jId);
    }
}
