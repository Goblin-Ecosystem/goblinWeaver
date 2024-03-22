package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.ValueObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Freshness extends AbstractAddedValue<Map<String, String>>{


    public Freshness(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum(){
        return AddedValueEnum.FRESHNESS;
    }

    @Override
    public void computeValue() {
        this.value = getFreshnessMapFromGav(nodeId);
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
