package com.cifre.sap.su.neo4jEcosystemWeaver.utils;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.Weaver;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4jUtils {

    public static Map<String, Object> generateNodeRow(Pair<String, Value> pair, List<AddedValueEnum> addedValues){
        Node node = pair.value().asNode();
        Map<String, Object> nodeMap = new HashMap<>(node.asMap());
        nodeMap.put("labels", node.labels());
        nodeMap.put("elementId", node.elementId());
        Weaver.fillNode(nodeMap, addedValues);
        return nodeMap;
    }

    public static Map<String, Object> generateRelationshipRow(Pair<String, Value> pair, List<AddedValueEnum> addedValues){
        Relationship relationship = pair.value().asRelationship();
        Map<String, Object> relationshipMap = new HashMap<>(relationship.asMap());
        relationshipMap.put("type", relationship.type());
        relationshipMap.put("sourceElementId", relationship.startNodeElementId());
        relationshipMap.put("targetElementId", relationship.endNodeElementId());
        Weaver.fillRelationship(relationshipMap, addedValues);
        return relationshipMap;
    }

    public static Map<String, Object> generatePathRow(Pair<String, Value> pair, List<AddedValueEnum> addedValues){
        Map<String, Object> row = new HashMap<>();
        Path path = pair.value().asPath();
        for (Node node : path.nodes()){
            Map<String, Object> nodeMap = new HashMap<>(node.asMap());
            nodeMap.put("labels", node.labels());
            nodeMap.put("elementId", node.elementId());
            Weaver.fillNode(nodeMap, addedValues);
            row.put("node", nodeMap);
        }
        for (Relationship relationship : path.relationships()){
            Map<String, Object> relationshipMap = new HashMap<>(relationship.asMap());
            relationshipMap.put("type", relationship.type());
            relationshipMap.put("sourceElementId", relationship.startNodeElementId());
            relationshipMap.put("targetElementId", relationship.endNodeElementId());
            Weaver.fillRelationship(relationshipMap, addedValues);
            row.put("relationship", relationshipMap);
        }
        return row;
    }
}
