package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.graphController;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNodeWithAddedValues {
    public static Map<AddedValueEnum, String> getReleaseAddedValuesFromGav(String gav, List<AddedValueEnum> addedValues){
        // Get desired addedValues
        StringBuilder matchQuery = new StringBuilder("MATCH (r:Release) WHERE r.id = '" + gav + "' ");
        matchQuery.append("OPTIONAL MATCH (r)-[l:addedValues]->(v:AddedValue) WHERE ");
        boolean firstType = true;
        for (AddedValueEnum type : addedValues) {
            if (!firstType) {
                matchQuery.append(" OR ");
            } else {
                firstType = false;
            }
            matchQuery.append("v.type = '").append(type).append("'");
        }
        matchQuery.append(" RETURN v.type AS type, v.value AS value");
        String query = matchQuery.toString();
        return execRequest(query);
    }

    public static Map<AddedValueEnum, String> getArtifactAddedValuesFromGav(String ga, List<AddedValueEnum> addedValues) {
        // Get desired addedValues
        StringBuilder matchQuery = new StringBuilder("MATCH (a:Artifact) WHERE a.id = '" + ga + "' ");
        matchQuery.append("OPTIONAL MATCH (a)-[l:addedValues]->(v:AddedValue) WHERE ");
        boolean firstType = true;
        for (AddedValueEnum type : addedValues) {
            if (!firstType) {
                matchQuery.append(" OR ");
            } else {
                firstType = false;
            }
            matchQuery.append("v.type = '").append(type).append("'");
        }
        matchQuery.append(" RETURN v.type AS type, v.value AS value");
        String query = matchQuery.toString();
        return execRequest(query);
    }

    private static Map<AddedValueEnum, String> execRequest(String query){
        Map<AddedValueEnum, String> typeValueMap = new HashMap<>();
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                AddedValueEnum type = null;
                String value = null;
                for (Pair<String, Value> pair : record.fields()) {
                    if(!pair.value().toString().equals("NULL")) {
                        if ("type".equals(pair.key())) {
                            type = AddedValueEnum.valueOf(pair.value().toString().replaceAll("[\"]",""));
                        } else {
                            String brutValue = pair.value().toString();
                            value = brutValue.substring(1, brutValue.length() - 1).replace("\\", "");
                        }
                    }
                }
                if (type != null && value != null) {
                    typeValueMap.put(type, value);
                }
            }
            return typeValueMap;
        }
    }
}
