package com.cifre.sap.su.goblinWeaver.weaver;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.*;

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
                        if(!addedValueEnum.isAggregatedValue()){
                            computedAddedValues.add(addedValue);
                        }
                    }
                    node.addAddedValue(addedValue);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return computedAddedValues;
    }
}
