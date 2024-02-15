package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.util.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Freshness implements AddedValue<Map<String, String>>{
    protected final String gav;
    protected Map<String, String> value;

    public Freshness(String gav) {
        this.gav = gav;
    }

    public AddedValueEnum getAddedValueEnum(){
        return AddedValueEnum.FRESHNESS;
    }

    public String getNodeId(){
        return gav;
    }

    @Override
    public Map<String, Object> getValue() {
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public void setValue(String value) {
        this.value = this.stringToValue(value);
    }

    @Override
    public void computeValue() {
        this.value = fillFreshness();
        //GraphUtils.putReleaseAddedValueOnGraph(gav, getAddedValueEnum(), valueToString());
    }

    private Map<String, String> fillFreshness() {
        return getFreshnessMapFromGav(gav);
    }

    @Override
    public Map<String, String> stringToValue(String jsonString){
        Map<String, String> resultMap = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            JSONObject freshnessJson = (JSONObject) jsonObject.get(getAddedValueEnum().getJsonKey());
            resultMap.put("numberMissedRelease", (String) freshnessJson.get("numberMissedRelease"));
            resultMap.put("outdatedTimeInMs", (String) freshnessJson.get("outdatedTimeInMs"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public String valueToString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(value);
        JSONObject finalObject = new JSONObject();
        finalObject.put(getAddedValueEnum().getJsonKey(), value);
        return finalObject.toJSONString().replace("\"", "\\\"");
    }

    protected static Map<String, String> getFreshnessMapFromGav(String gav){
        Map<String, String> freshnessMap = new HashMap<>();
        String query = "MATCH (r1:Release)<-[:relationship_AR]-(:Artifact)-[:relationship_AR]->(r2:Release) " +
                "WHERE r1.id = '"+gav+"' AND r2.timestamp > r1.timestamp " +
                "WITH r2, r2.timestamp - r1.timestamp AS difference " +
                "RETURN count(r2) AS numberMissedRelease, max(difference) AS outdatedTimeInMs";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                for (Pair<String, Value> pair : record.fields()) {
                    String value = pair.value().toString().equals("NULL") ? "0" : pair.value().toString();
                    freshnessMap.put(pair.key(),value);
                }
            }
        }
        return freshnessMap;
    }
}
