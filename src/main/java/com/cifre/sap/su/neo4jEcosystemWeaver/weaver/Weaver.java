package com.cifre.sap.su.neo4jEcosystemWeaver.weaver;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.*;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.graphController.GetNodeWithAddedValues;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Weaver {

    public static void weaveGraph (InternGraph graph, List<AddedValueEnum> addedValues){
        if(addedValues.isEmpty()){
            return;
        }
        List<AddedValue<?>> computedAddedValues = new ArrayList<>();
        for(NodeType nodeType : NodeType.values()){
            Set<AddedValueEnum> nodeTypeAddedValues = addedValues.stream().filter(a -> a.getTargetNodeType().equals(nodeType)).collect(Collectors.toSet());
            if(!nodeTypeAddedValues.isEmpty()){
                Set<String> nodeIds = graph.getGraphNodes().stream().filter(node -> node.getType().equals(nodeType)).map(NodeObject::getId).collect(Collectors.toSet());
                if(!nodeIds.isEmpty()){
                    Map<String,Map<AddedValueEnum,String>> resolvedNodeAddedValues = GraphDatabaseSingleton.getInstance().getNodeAddedValues(nodeIds, addedValues, nodeType);
                    computedAddedValues.addAll(fillNodeAddedValues(graph, nodeTypeAddedValues, resolvedNodeAddedValues, nodeType));
                }
            }
        }
        GraphDatabaseSingleton.getInstance().addAddedValues(computedAddedValues);
    }

    private static List<AddedValue<?>> fillNodeAddedValues(InternGraph graph, Set<AddedValueEnum> nodeTypeAddedValues, Map<String,Map<AddedValueEnum,String>> resolvedNodeAddedValues, NodeType nodeType) {
        List<AddedValue<?>> computedAddedValues = new ArrayList<>();
        for (NodeObject node : graph.getGraphNodes().stream().filter(node -> node.getType().equals(nodeType)).collect(Collectors.toSet())){
            String nodeId = node.getId();
            for(AddedValueEnum addedValueEnum : nodeTypeAddedValues){
                try {
                    AddedValue<?> addedValue = addedValueEnum.getAddedValueClass().getDeclaredConstructor(String.class).newInstance(nodeId);
                    // If addedValue is present on graph
                    if(resolvedNodeAddedValues.containsKey(nodeId) && resolvedNodeAddedValues.get(nodeId).containsKey(addedValueEnum)){
                        addedValue.setValue(resolvedNodeAddedValues.get(nodeId).get(addedValueEnum));
                    }
                    else{
                        addedValue.computeValue();
                        computedAddedValues.add(addedValue);
                    }
                    node.addAddedValue(addedValue);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return computedAddedValues;
    }

    public static void fillNode(Map<String, Object> nodeMap, List<AddedValueEnum> addedValues){
        String nodeId = (String) nodeMap.get("id");
        String nodeType = ( (List<String>) nodeMap.get("labels")).get(0);
        for(AddedValue addedValue : getNodeValuesToAdd(nodeId, nodeType, addedValues)){
            //Map<String, Object> value = addedValue.getValue();
            //nodeMap.put(value.key(), value.value());
        }
    }

    public static void fillRelationship(Map<String, Object> relationshipMap, List<AddedValueEnum> addedValues){
        /*String relationshipType = (String) relationshipMap.get("type");
        for(AddedValue addedValue : getRelationshipValuesToAdd(relationshipType, addedValues)){
            Pair<String, Object> value = addedValue.getValue();
            relationshipMap.put(value.key(), value.value());
        }*/
    }

    private static Set<AddedValue> getNodeValuesToAdd(String nodeId, String nodeType, List<AddedValueEnum> addedValues){
        Set<AddedValue> set = new HashSet<>();
        if(addedValues.isEmpty()){
            return set;
        }
        // Release vertices
        if(nodeType.equals("Release")){
            Map<AddedValueEnum,String> alreadyCalculatedAddedValues = GetNodeWithAddedValues.getReleaseAddedValuesFromGav(nodeId, addedValues);
            //Put release added values here
            if(addedValues.contains(AddedValueEnum.CVE)){
                AddedValue cveValue = new Cve(nodeId);
                if(alreadyCalculatedAddedValues.containsKey(AddedValueEnum.CVE)){
                    cveValue.setValue(alreadyCalculatedAddedValues.get(AddedValueEnum.CVE));
                }
                else{
                    cveValue.computeValue();
                }
                set.add(cveValue);
            }
            if(addedValues.contains(AddedValueEnum.CVE_AGGREGATED)){
                AddedValue cveAggregatedValue = new CveAggregated(nodeId);
                if(alreadyCalculatedAddedValues.containsKey(AddedValueEnum.CVE_AGGREGATED)){
                    cveAggregatedValue.setValue(alreadyCalculatedAddedValues.get(AddedValueEnum.CVE_AGGREGATED));
                }
                else{
                    cveAggregatedValue.computeValue();
                }
                set.add(cveAggregatedValue);
            }
            if(addedValues.contains(AddedValueEnum.FRESHNESS)){
                AddedValue freshnessObject = new Freshness(nodeId);
                if(alreadyCalculatedAddedValues.containsKey(AddedValueEnum.FRESHNESS)){
                    freshnessObject.setValue(alreadyCalculatedAddedValues.get(AddedValueEnum.FRESHNESS));
                }
                else{
                    freshnessObject.computeValue();
                }
                set.add(freshnessObject);
            }
            if(addedValues.contains(AddedValueEnum.FRESHNESS_AGGREGATED)){
                AddedValue freshnessAggregatedValue = new FreshnessAggregated(nodeId);
                if(alreadyCalculatedAddedValues.containsKey(AddedValueEnum.FRESHNESS_AGGREGATED)){
                    freshnessAggregatedValue.setValue(alreadyCalculatedAddedValues.get(AddedValueEnum.FRESHNESS_AGGREGATED));
                }
                else{
                    freshnessAggregatedValue.computeValue();
                }
                set.add(freshnessAggregatedValue);
            }
        }
        // Artifact vertices
        if(nodeType.equals("Artifact")){
            Map<AddedValueEnum,String> alreadyCalculatedAddedValues = GetNodeWithAddedValues.getArtifactAddedValuesFromGav(nodeId, addedValues);
            //Put artifact added values here
            AddedValue speedObject = new Speed(nodeId);
            if(alreadyCalculatedAddedValues.containsKey(AddedValueEnum.SPEED)){
                speedObject.setValue(alreadyCalculatedAddedValues.get(AddedValueEnum.SPEED));
            }
            else{
                speedObject.computeValue();
            }
            set.add(speedObject);
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
