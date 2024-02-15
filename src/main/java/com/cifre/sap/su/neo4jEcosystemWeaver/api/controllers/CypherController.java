package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.api.entities.CypherQuery;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Cypher query")
public class CypherController {

    @Operation(
            description = "Execute a cypher query to the dependency graph with added values",
            summary = "Execute a cypher query"
    )
    @PostMapping("/cypher")
    public JSONObject executeCypherQuery(@RequestBody CypherQuery queryRequest) {
        InternGraph graph = GraphDatabaseSingleton.getInstance().executeQuery(queryRequest.getQuery());
        Weaver.weaveGraph(graph, queryRequest.getAddedValues());
        return graph.getJsonGraph();
    }
}
