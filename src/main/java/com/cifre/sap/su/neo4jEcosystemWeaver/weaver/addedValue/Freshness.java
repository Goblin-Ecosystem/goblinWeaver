package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.ValueObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    public Map<String, Object> getValueMap() {
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public Map<String, String> getValue(){
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = this.stringToValue(value);
    }

    @Override
    public void computeValue() {
        this.value = getFreshnessMapFromGav(gav);
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
    public String valueToString(Map<String, String> value){
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(value);
        JSONObject finalObject = new JSONObject();
        finalObject.put(getAddedValueEnum().getJsonKey(), value);
        return finalObject.toJSONString().replace("\"", "\\\"");
    }

    protected static Map<String, String> getFreshnessMapFromGav(String gav){
        Map<String, String> freshnessMap = new HashMap<>();
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseFreshness(gav));
        for(ValueObject value : graph.getGraphValues()){
            String valueNotNull = value.getValue().equals("NULL") ? "0" : value.getValue();
            freshnessMap.put(value.getKey(),valueNotNull);
        }
        return freshnessMap;
    }
}
