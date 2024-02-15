package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.api.entities.CypherQuery;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;
import org.neo4j.driver.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Tag(name = "Cypher query")
public class CypherController {

    @Operation(
            description = "Execute a cypher query to the dependency graph with added values",
            summary = "Execute a cypher query"
    )
    @PostMapping("/cypher")
    public List<Map<String, Object>> executeCypherQuery(@RequestBody CypherQuery queryRequest) {
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        try (Session session = driver.session()) {
            Result result = session.run(queryRequest.getQuery());
            List<Map<String, Object>> rows = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> row = new HashMap<>();
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        row.put("node", Neo4jUtils.generateNodeRow(pair, queryRequest.getAddedValues()));
                    }
                    else if (pair.value().hasType(TypeSystem.getDefault().RELATIONSHIP())){
                        row.put("relationship", Neo4jUtils.generateRelationshipRow(pair, queryRequest.getAddedValues()));
                    }
                    else if (pair.value().hasType(TypeSystem.getDefault().PATH())) {
                        row.putAll(Neo4jUtils.generatePathRow(pair, queryRequest.getAddedValues()));
                    }
                    else{
                        row.put(pair.key(), pair.value().toString().replaceAll("[\"]",""));
                    }
                }
                rows.add(row);
            }
            return rows;
        }
    }

    @PostMapping("/test")
    public JSONObject executeTest(@RequestBody CypherQuery queryRequest) {
        InternGraph graph = GraphDatabaseSingleton.getInstance().executeQuery(queryRequest.getQuery());
        Weaver.weaveGraph(graph, queryRequest.getAddedValues());
        return graph.getJsonGraph();
    }
}
