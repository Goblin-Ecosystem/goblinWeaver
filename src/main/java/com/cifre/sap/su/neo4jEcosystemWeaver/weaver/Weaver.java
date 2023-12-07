package com.cifre.sap.su.neo4jEcosystemWeaver.weaver;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.*;
import org.neo4j.driver.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Weaver {

    public static void fillNode(Map<String, Object> nodeMap, List<AddedValueEnum> addedValues){
        String nodeId = (String) nodeMap.get("id");
        String nodeType = ( (List<String>) nodeMap.get("labels")).get(0);
        for(AddedValue addedValue : getNodeValuesToAdd(nodeId, nodeType, addedValues)){
            Pair<String, Object> value = addedValue.getValue();
            nodeMap.put(value.key(), value.value());
        }
    }

    public static void fillRelationship(Map<String, Object> relationshipMap, List<AddedValueEnum> addedValues){
        String relationshipType = (String) relationshipMap.get("type");
        for(AddedValue addedValue : getRelationshipValuesToAdd(relationshipType, addedValues)){
            Pair<String, Object> value = addedValue.getValue();
            relationshipMap.put(value.key(), value.value());
        }
    }

    private static Set<AddedValue> getNodeValuesToAdd(String nodeId, String nodeType, List<AddedValueEnum> addedValues){
        Set<AddedValue> set = new HashSet<>();
        // Release vertices
        if(nodeType.equals("Release")){
            //Put release added values here
            if(addedValues.contains(AddedValueEnum.CVE)){
                set.add(new Cve(nodeId));
            }
            if(addedValues.contains(AddedValueEnum.CVE_AGGREGATED)){
                set.add(new CveAggregated(nodeId));
            }
            if(addedValues.contains(AddedValueEnum.FRESHNESS)){
                set.add(new Freshness(nodeId));
            }
            if(addedValues.contains(AddedValueEnum.FRESHNESS_AGGREGATED)){
                set.add(new FreshnessAggregated(nodeId));
            }
        }
        // Artifact vertices
        if(nodeType.equals("Artifact")){
            //Put artifact added values here
            if(addedValues.contains(AddedValueEnum.SPEED)){
                set.add(new Speed(nodeId));
            }
        }
        return set;
    }

    private static Set<AddedValue> getRelationshipValuesToAdd(String relationshipType, List<AddedValueEnum> addedValues){
        Set<AddedValue> set = new HashSet<>();
        if(relationshipType.equals("dependency")){
            //Put dependency added values here
        }
        if(relationshipType.equals("relationship_AR")){
            //Put relationship_AR added values here
        }
        return set;
    }
}
