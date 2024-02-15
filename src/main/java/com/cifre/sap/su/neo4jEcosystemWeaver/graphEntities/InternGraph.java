package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.EdgeObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternGraph {
    private final Set<NodeObject> graphNodes = new HashSet<>();
    private final Set<EdgeObject> graphEdges = new HashSet<>();
    private final Set<ValueObject> graphValues = new HashSet<>();

    public InternGraph() {
    }

    public void addNode(NodeObject node){
        this.graphNodes.add(node);
    }

    public void addEdge(EdgeObject edge){
        this.graphEdges.add(edge);
    }

    public void addValue(ValueObject value){
        this.graphValues.add(value);
    }

    public Set<NodeObject> getGraphNodes() {
        return graphNodes;
    }

    public JSONObject getJsonGraph(){
        resolveEdgesId();
        JSONObject graphJSON = new JSONObject();
        addObjectsToJSONArray(graphNodes, "nodes", graphJSON);
        addObjectsToJSONArray(graphEdges, "edges", graphJSON);
        addObjectsToJSONArray(graphValues, "values", graphJSON);
        return graphJSON;
    }

    private void addObjectsToJSONArray(Set<? extends GraphObject> objects, String key, JSONObject graphJSON) {
        if (!objects.isEmpty()) {
            JSONArray array = new JSONArray();
            for (GraphObject obj : objects) {
                array.add(obj.getJsonObject());
            }
            graphJSON.put(key, array);
        }
    }

    private void resolveEdgesId(){
        Map<String, String> nodeMap = new HashMap<>();
        for (NodeObject node : graphNodes) {
            nodeMap.put(node.getNeo4jId(), node.getId());
        }

        for (EdgeObject edge : graphEdges) {
            String sourceId = nodeMap.get(edge.getSourceId());
            String targetId = nodeMap.get(edge.getTargetId());

            // Mettre à jour les identifiants des arêtes en conséquence
            if (sourceId != null) {
                edge.setSourceId(sourceId);
            }
            if (targetId != null) {
                edge.setTargetId(targetId);
            }
        }
    }

    public void printGraph(){
        System.out.println("Nodes: ");
        for (NodeObject node : graphNodes){
            System.out.println("\t"+node.getId());
        }
        for (ValueObject value : graphValues){
            System.out.println("\t"+value.toString());
        }
    }
}
