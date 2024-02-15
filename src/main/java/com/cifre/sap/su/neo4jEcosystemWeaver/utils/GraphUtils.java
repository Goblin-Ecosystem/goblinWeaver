package com.cifre.sap.su.neo4jEcosystemWeaver.utils;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class GraphUtils {

    public static void putReleaseAddedValueOnGraph(String gav, AddedValueEnum addedValueType, String value){
        String query = "MATCH (r:Release {id:'"+gav+"'}) " +
                "CREATE (r)-[l:addedValues]->(v:AddedValue {type: \""+addedValueType+"\", value:\""+value+"\"})";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            session.run(query);
        }
    }

    public static void putArtifactAddedValueOnGraph(String ga, AddedValueEnum addedValueType, String value){
        String query = "MATCH (a:Artifact {id:'"+ga+"'}) " +
                "CREATE (a)-[l:addedValues]->(v:AddedValue {type: \""+addedValueType+"\", value:\""+value+"\"})";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            session.run(query);
        }
    }

    public static void removeAddedValuesOnGraph(Set<AddedValueEnum> addedValuesType){
        StringBuilder cypherQuery = new StringBuilder();
        cypherQuery.append("MATCH ()-[r]-(n:AddedValue)\n")
                .append("WHERE n.type IN [");
        int i = 0;
        for (AddedValueEnum type : addedValuesType) {
            cypherQuery.append("'").append(type).append("'");
            if (++i < addedValuesType.size()) {
                cypherQuery.append(", ");
            }
        }
        cypherQuery.append("]\n")
                .append("DELETE r, n;");

        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            session.run(cypherQuery.toString());
        }
    }

    public static Set<String> getGavAndDependenciesSetWithTransitive(String gav, Set<String> visitedGav){
        if (visitedGav.contains(gav)) {
            return visitedGav;
        }
        visitedGav.add(gav);
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
                getGavAndDependenciesSetWithTransitive(newGav, visitedGav);
            }
        }
        return visitedGav;
    }
}
