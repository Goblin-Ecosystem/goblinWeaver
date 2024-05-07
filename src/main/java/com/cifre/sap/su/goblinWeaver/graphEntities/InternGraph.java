package com.cifre.sap.su.goblinWeaver.graphEntities;

import com.cifre.sap.su.goblinWeaver.graphEntities.edges.EdgeObject;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class InternGraph {
    private final Set<NodeObject> graphNodes = new HashSet<>();
    private final Set<EdgeObject> graphEdges = new HashSet<>();
    private Set<ValueObject> graphValues = new HashSet<>();

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

    public Set<ValueObject> getGraphValues() {
        return graphValues;
    }

    public Set<EdgeObject> getGraphEdges() {
        return graphEdges;
    }

    public JSONObject getJsonGraph(){
        resolveEdgesId();
        JSONObject graphJSON = new JSONObject();
        addObjectsToJSONArray(graphNodes, "nodes", graphJSON);
        addObjectsToJSONArray(graphEdges, "edges", graphJSON);
        addObjectsToJSONArray(graphValues, "values", graphJSON);
        return graphJSON;
    }

    public void mergeGraph(InternGraph graph){
        this.graphNodes.addAll(graph.getGraphNodes());
        this.graphEdges.addAll(graph.getGraphEdges());
        this.graphValues.addAll(graph.getGraphValues());
    }

    public void clearValueNodes(){
        this.graphValues = new HashSet<>();
    }

    private void addObjectsToJSONArray(Set<? extends GraphObject> objects, String key, JSONObject graphJSON) {
        if (!objects.isEmpty()) {
            JSONArray array = new JSONArray();
            Iterator<? extends GraphObject> iterator = objects.iterator();
            while (iterator.hasNext()) {
                GraphObject obj = iterator.next();
                array.add(obj.getJsonObject());
                iterator.remove();
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
}
