package com.cifre.sap.su.neo4jEcosystemWeaver.utils;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class GraphUtils {

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
