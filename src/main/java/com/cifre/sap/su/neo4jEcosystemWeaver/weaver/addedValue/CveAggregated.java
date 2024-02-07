package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.graphController.GetNodeWithAddedValues;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CveAggregated extends Cve {

    public CveAggregated(String gav) {
        super(gav);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.CVE_AGGREGATED;
    }

    @Override
    public void computeValue() {
        super.value = fillAggregatedCve(super.gav);
    }

    private Set<Map<String, String>> fillAggregatedCve(String gav){
        // Check if value exist
        Map<AddedValueEnum,String> alreadyCalculatedAddedValues = GetNodeWithAddedValues.getReleaseAddedValuesFromGav(gav, List.of(getAddedValueEnum()));
        // Value exist
        if(alreadyCalculatedAddedValues.containsKey(getAddedValueEnum())){
            return this.stringToValue(alreadyCalculatedAddedValues.get(getAddedValueEnum()));
        }
        else{
            // Add release CVEs
            Set<Map<String, String>> aggregatedCveValue = new HashSet<>(getCveFromGav(gav));
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
                    aggregatedCveValue.addAll(fillAggregatedCve(newGav));
                }
            }
            //Add calculated value on graph and return
            GraphUtils.putReleaseAddedValueOnGraph(gav, getAddedValueEnum(), valueToString(aggregatedCveValue));
            return aggregatedCveValue;
        }
    }
}
