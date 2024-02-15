package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.api.entities.ArtifactQuery;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;
import org.neo4j.driver.util.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Artifacts")
public class ArtifactController {

    @Operation(
            description = "Get a specific artifact from groupId:ArtifactId with added values",
            summary = "Get a specific artifact from GA"
    )
    @PostMapping("/artifact")
    public List<Map<String, Object>> getSpecificArtifact(@RequestBody ArtifactQuery artifactQuery) {
        String query = "MATCH (a:Artifact) " +
                "WHERE a.id = '" + artifactQuery.toString() + "' " +
                "RETURN a";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            List<Map<String, Object>> rows = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> row = new HashMap<>();
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        Map<String, Object> nodeMap = Neo4jUtils.generateNodeRow(pair, artifactQuery.getAddedValues());
                        row.put("node", nodeMap);
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }

    @Operation(
            description = "Get all releases of an artifact from groupId:ArtifactId with added values",
            summary = "Get all releases of an artifact from GA"
    )
    @PostMapping("/artifact/releases")
    public List<Map<String, Object>> getArtifactReleases(@RequestBody ArtifactQuery artifactQuery) {
        String query = "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                "WHERE a.id = '" + artifactQuery.toString() + "' " +
                "RETURN r";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            List<Map<String, Object>> rows = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> row = new HashMap<>();
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        Map<String, Object> nodeMap = Neo4jUtils.generateNodeRow(pair, artifactQuery.getAddedValues());
                        row.put("node", nodeMap);
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }
}
