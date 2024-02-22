package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;

import java.util.*;

public class CveAggregated extends Cve {

    public CveAggregated(String gav) {
        super(gav);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.CVE_AGGREGATED;
    }

    public String getNodeId(){
        return gav;
    }

    @Override
    public void computeValue() {
        super.value = fillAggregatedCve(super.gav, new HashSet<>());
    }

    private Set<Map<String, String>> fillAggregatedCve(String gav, Set<String> visiting){
        // For cycles
        if(visiting.contains(gav)){
            return new HashSet<>();
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
            // Add release CVEs
            Set<Map<String, String>> aggregatedCveValue = new HashSet<>(getCveFromGav(gav));
            // Query to get the dependencies of the given release without test dependencies
            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependencies(gav));
            for(NodeObject dep : graph.getGraphNodes()){
                aggregatedCveValue.addAll(fillAggregatedCve(dep.getId(), visiting));
            }
            visiting.remove(gav);
            //Add calculated value on graph and return
            gdb.putOneAddedValueOnGraph(gav, getAddedValueEnum(), valueToString(aggregatedCveValue));
            return aggregatedCveValue;
        }
    }
}
