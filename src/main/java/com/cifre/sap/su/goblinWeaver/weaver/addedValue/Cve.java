package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.OsvDataSingleton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Cve implements AddedValue<Set<Map<String, String>>>{
    protected final String gav;
    protected Set<Map<String, String>> value;

    public Cve(String gav) {
        this.gav = gav;
    }

    public AddedValueEnum getAddedValueEnum(){
        return AddedValueEnum.CVE;
    }

    public String getNodeId(){
        return gav;
    }

    public void setValue(String value){
        this.value = this.stringToValue(value);
    }

    public void computeValue(){
        value = fillCve();
    }

    @Override
    public Map<String, Object> getValueMap() {
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public Set<Map<String, String>> getValue(){
        return value;
    }

    @Override
    public Set<Map<String, String>> stringToValue(String jsonString){
        Set<Map<String, String>> resultSet = new HashSet<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            JSONArray cveArray = (JSONArray) jsonObject.get(getAddedValueEnum().getJsonKey());
            if(cveArray != null) {
                for (Object obj : cveArray) {
                    JSONObject cveJson = (JSONObject) obj;

                    Map<String, String> cveMap = Map.of(
                            "cwe", (String) cveJson.get("cwe"),
                            "severity", (String) cveJson.get("severity"),
                            "name", (String) cveJson.get("name")
                    );

                    resultSet.add(cveMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    @Override
    public String valueToString(Set<Map<String, String>> value){
        JSONArray jsonArray = new JSONArray();

        for (Map<String, String> map : value) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(map);
            jsonArray.add(jsonObject);
        }

        JSONObject finalObject = new JSONObject();
        finalObject.put(getAddedValueEnum().getJsonKey(), jsonArray);

        return finalObject.toJSONString().replace("\"", "\\\"");
    }

    private Set<Map<String, String>> fillCve(){
        return getCveFromGav(gav);
    }

    protected static Set<Map<String, String>> getCveFromGav(String gav){
        Set<Map<String, String>> resultSet = new HashSet<>();
        try {
            JSONObject jsonObject = OsvDataSingleton.getDataJsonObject();
            JSONArray cveArray = (JSONArray) jsonObject.get(gav);
            if (cveArray != null) {
                for (Object vulnerability : cveArray) {
                    JSONObject vulnerabilityJsonObject = (JSONObject) vulnerability;
                    Map<String, String> cveMap = new HashMap();
                    cveMap.put("name", (String) vulnerabilityJsonObject.get("name"));
                    cveMap.put("cwe", (String) vulnerabilityJsonObject.get("cwe_ids"));
                    cveMap.put("severity", (String) vulnerabilityJsonObject.get("severity"));
                    resultSet.add(cveMap);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
