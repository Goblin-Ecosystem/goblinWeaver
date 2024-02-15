package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.graphController.GetNodeWithAddedValues;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

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
        super.value = fillAggregatedFreshness(super.gav);
    }

    private Map<String, String> fillAggregatedFreshness(String gav){
        // Check if value exist
        Map<AddedValueEnum,String> alreadyCalculatedAddedValues = GetNodeWithAddedValues.getReleaseAddedValuesFromGav(gav, List.of(getAddedValueEnum()));
        // Value exist
        if(alreadyCalculatedAddedValues.containsKey(getAddedValueEnum())){
            return this.stringToValue(alreadyCalculatedAddedValues.get(getAddedValueEnum()));
        }
        else{
            // Compute release freshness
            int totalNumberMissedRelease = 0;
            long totalOutdatedTimeInMs = 0;
            Map<String, String> currentFreshnessValue = new HashMap<>(getFreshnessMapFromGav(gav));
            totalNumberMissedRelease += Integer.parseInt(currentFreshnessValue.get("numberMissedRelease"));
            totalOutdatedTimeInMs += Long.parseLong(currentFreshnessValue.get("outdatedTimeInMs"));
            // Query to get the dependencies of the given release without test dependencies
            String query = "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                    "WHERE r.id = '" + gav + "' AND NOT (d.scope = 'test') " +
                    "RETURN a.id AS artifactId, d.targetVersion AS targetVersion";
            Driver driver = Neo4jDriverSingleton.getDriverInstance();
            try (Session session = driver.session()) {
                Result result = session.run(query);
                while (result.hasNext()) {
                    Record record = result.next();
                    String newGav = record.get("artifactId").toString().replaceAll("[\"]","")
                            +":"
                            +record.get("targetVersion").toString().replaceAll("[\"]","");
                    Map<String, String> freshnessToAdd = fillAggregatedFreshness(newGav);
                    totalNumberMissedRelease += Integer.parseInt(freshnessToAdd.get("numberMissedRelease"));
                    totalOutdatedTimeInMs += Long.parseLong(freshnessToAdd.get("outdatedTimeInMs"));
                }
            }
            Map<String, String> aggregatedFreshnessMap = new HashMap<>();
            aggregatedFreshnessMap.put("numberMissedRelease", Integer.toString(totalNumberMissedRelease));
            aggregatedFreshnessMap.put("outdatedTimeInMs", Long.toString(totalOutdatedTimeInMs));
            //Add calculated value on graph and return
            GraphUtils.putReleaseAddedValueOnGraph(gav, getAddedValueEnum(), valueToString());
            return aggregatedFreshnessMap;
        }
    }
}
