package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.api.entities.ReleaseQuery;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jDriverSingleton;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Releases")
public class ReleaseController {

    @Operation(
            description = "Get a specific release from groupId:ArtifactId:Version with added values",
            summary = "Get a specific release from GAV"
    )
    @PostMapping("/release")
    public List<Map<String, Object>> getSpecificRelease(@RequestBody ReleaseQuery releaseQuery) {
        String query = "MATCH (r:Release) " +
                "WHERE r.id = '" + releaseQuery.toString() + "' " +
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
                        Map<String, Object> nodeMap = Neo4jUtils.generateNodeRow(pair, releaseQuery.getAddedValues());
                        row.put("node", nodeMap);
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }

    @Operation(
            description = "Get newer versions of a release from groupId:ArtifactId:Version with added values",
            summary = "Get newer versions of a release from GAV"
    )
    @PostMapping("/release/newVersions")
    public List<Map<String, Object>> getNewerReleases(@RequestBody ReleaseQuery releaseQuery) {
        String queryTimestamp = "MATCH (r:Release) " +
                "WHERE r.id = '" + releaseQuery.toString() + "' " +
                "RETURN r.timestamp as timestamp";
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result resultTimestamp = session.run(queryTimestamp);
            long timestamp = resultTimestamp.next().fields().get(0).value().asLong();
            String queryNewReleases = "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                    "WHERE a.id = '" + releaseQuery.getGa() + "' AND r.timestamp > "+timestamp+" " +
                    "RETURN r";
            Result resultNewReleases = session.run(queryNewReleases);
            List<Map<String, Object>> rows = new ArrayList<>();
            while (resultNewReleases.hasNext()) {
                Record record = resultNewReleases.next();
                Map<String, Object> row = new HashMap<>();
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        Map<String, Object> nodeMap = Neo4jUtils.generateNodeRow(pair, releaseQuery.getAddedValues());
                        row.put("node", nodeMap);
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }

    @Operation(
            description = "Get dependents of a release from groupId:ArtifactId:Version with added values",
            summary = "Get release dependents from GAV"
    )
    @PostMapping("/release/dependents")
    public List<Map<String, Object>> getReleaseDependent(@RequestBody ReleaseQuery releaseQuery) {
        String query = "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                "WHERE a.id = '"+releaseQuery.getGa()+"' AND d.targetVersion = '"+releaseQuery.getVersion()+"' " +
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
                        Map<String, Object> nodeMap = Neo4jUtils.generateNodeRow(pair, releaseQuery.getAddedValues());
                        row.put("node", nodeMap);
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }
}
