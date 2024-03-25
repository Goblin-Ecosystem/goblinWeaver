package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Popularity1YearAggregated extends Popularity1Year {

    public Popularity1YearAggregated(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.POPULARITY_1_YEAR_AGGREGATED;
    }

    @Override
    public void computeValue() {
        super.value = fillAggregatedFreshness(nodeId, new HashSet<>());
    }

    private int fillAggregatedFreshness(String nodeId, Set<String> visiting){
        // For cycles
        if(visiting.contains(nodeId)){
            return 0;
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
            int aggregatedPopularity1YearValue = super.fillPopularity1Year(nodeId);
            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependencies(nodeId));
            for(NodeObject dep : graph.getGraphNodes()){
                aggregatedPopularity1YearValue += fillAggregatedFreshness(dep.getId(), visiting);
            }
            visiting.remove(nodeId);
            //Add calculated value on graph and return
            gdb.putOneAddedValueOnGraph(nodeId, getAddedValueEnum(), valueToString(aggregatedPopularity1YearValue));
            return aggregatedPopularity1YearValue;
        }
    }
}
