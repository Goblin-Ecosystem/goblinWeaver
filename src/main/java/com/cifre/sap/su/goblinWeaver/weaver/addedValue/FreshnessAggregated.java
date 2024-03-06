package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;

import java.util.*;

public class FreshnessAggregated extends Freshness{

    public FreshnessAggregated(String gav) {
        super(gav);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.FRESHNESS_AGGREGATED;
    }

    public String getNodeId(){
        return gav;
    }

    @Override
    public void computeValue() {
        super.value = fillAggregatedFreshness(super.gav, new HashSet<>());
    }

    private Map<String, String> fillAggregatedFreshness(String gav, Set<String> visiting){
        // For cycles
        if(visiting.contains(gav)){
            Map<String, String> emptyFreshness = new HashMap<>();
            emptyFreshness.put("numberMissedRelease", "0");
            emptyFreshness.put("outdatedTimeInMs", "0");
            return emptyFreshness;
        }
        visiting.add(gav);
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        // Check if value exist
        Map<String,Map<AddedValueEnum,String>> alreadyCalculatedAddedValue = gdb.getNodeAddedValues(Set.of(gav), List.of(getAddedValueEnum()), getAddedValueEnum().getTargetNodeType());
        // Value exist
        if(alreadyCalculatedAddedValue.containsKey(gav) && alreadyCalculatedAddedValue.get(gav).containsKey(getAddedValueEnum())){
            return this.stringToValue(alreadyCalculatedAddedValue.get(gav).get(getAddedValueEnum()));
        }
        else{
            // Compute release freshness
            int totalNumberMissedRelease = 0;
            long totalOutdatedTimeInMs = 0;
            Map<String, String> currentFreshnessValue = new HashMap<>(getFreshnessMapFromGav(gav));
            totalNumberMissedRelease += Integer.parseInt(currentFreshnessValue.get("numberMissedRelease"));
            totalOutdatedTimeInMs += Long.parseLong(currentFreshnessValue.get("outdatedTimeInMs"));

            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependencies(gav));
            for(NodeObject dep : graph.getGraphNodes()){
                Map<String, String> freshnessToAdd = fillAggregatedFreshness(dep.getId(), visiting);
                totalNumberMissedRelease += Integer.parseInt(freshnessToAdd.get("numberMissedRelease"));
                totalOutdatedTimeInMs += Long.parseLong(freshnessToAdd.get("outdatedTimeInMs"));
            }
            visiting.remove(gav);
            Map<String, String> aggregatedFreshnessMap = new HashMap<>();
            aggregatedFreshnessMap.put("numberMissedRelease", Integer.toString(totalNumberMissedRelease));
            aggregatedFreshnessMap.put("outdatedTimeInMs", Long.toString(totalOutdatedTimeInMs));
            //Add calculated value on graph and return
            gdb.putOneAddedValueOnGraph(gav, getAddedValueEnum(), valueToString(aggregatedFreshnessMap));
            return aggregatedFreshnessMap;
        }
    }
}
