package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AggregateValue<T> extends AddedValue<T>{

    default T computeAggregatedValue(String nodeId, Set<String> visiting){
        // For cycles
        if(visiting.contains(nodeId)){
            return getZeroValue();
        }
        visiting.add(nodeId);
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        // Check if value exist
        Map<String, Map<AddedValueEnum,String>> alreadyCalculatedAddedValue = gdb.getNodeAddedValues(List.of(nodeId), Set.of(getAddedValueEnum()), getAddedValueEnum().getTargetNodeType());
        // Value exist
        if(alreadyCalculatedAddedValue.containsKey(nodeId) && alreadyCalculatedAddedValue.get(nodeId).containsKey(getAddedValueEnum())){
            return this.stringToValue(alreadyCalculatedAddedValue.get(nodeId).get(getAddedValueEnum()));
        }
        else{
            T computedValue = computeMetric(nodeId);
            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependencies(nodeId));
            for(NodeObject dep : graph.getGraphNodes()){
                computedValue = mergeValue(computedValue, computeAggregatedValue(dep.getId(), visiting));
            }
            visiting.remove(nodeId);
            //Add calculated value on graph and return
            gdb.putOneAddedValueOnGraph(nodeId, getAddedValueEnum(), valueToString(computedValue));
            return computedValue;
        }
    }

    T mergeValue(T computedValue, T computeAggregatedValue);

    T computeMetric(String nodeId);

    T getZeroValue();
}
