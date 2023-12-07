package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.common;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.ApiUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.OsvDataSingleton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReusedCalculation {
    public static Set<Map<String, String>> getCveFromGav(String gav){
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

    public static Set<Map<String, String>> getCveFromApiWithGav(String gav){
        Set<Map<String, String>> resultSet = new HashSet<>();
        String[] splitedGav = gav.split(":");
        String packageName = splitedGav[0]+":"+splitedGav[1];
        String version = splitedGav[2];
        JSONObject jsonObject = ApiUtils.callWithData("https://api.osv.dev/v1/query", "POST", "{\"version\": \""+version+"\", \"package\": {\"name\": \""+packageName+"\", \"ecosystem\": \"Maven\"}}");
        if (jsonObject != null) {
            JSONArray vulnerabilities = (JSONArray) jsonObject.get("vulns");
            if (vulnerabilities != null) {
                for (Object vulnerability : vulnerabilities) {
                    JSONObject vulnerabilityJsonObject = (JSONObject) vulnerability;
                    String vulnerabilityName = vulnerabilityJsonObject.get("aliases") != null ? (vulnerabilityJsonObject.get("aliases").toString().split("\"]")[0].substring(2)) : "";
                    JSONObject detailJsonObject = (JSONObject) vulnerabilityJsonObject.get("database_specific");
                    String vulnerabilitySeverity = "";
                    String vulnerabilityCwe = "";
                    if(detailJsonObject != null){
                        vulnerabilitySeverity = detailJsonObject.get("severity") != null ? detailJsonObject.get("severity").toString() : "";
                        vulnerabilityCwe = detailJsonObject.get("cwe_ids") != null ? detailJsonObject.get("cwe_ids").toString() : "";
                    }
                    Map<String, String> cveMap = new HashMap();
                    cveMap.put("name", vulnerabilityName.replaceAll("[\"]",""));
                    cveMap.put("cwe", vulnerabilityCwe.replaceAll("[\"]",""));
                    cveMap.put("severity", vulnerabilitySeverity.replaceAll("[\"]",""));
                    resultSet.add(cveMap);
                }
            }
        }
        return resultSet;
    }

    public static Map<String, String> getFreshnessMapFromGav(String gav){
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
