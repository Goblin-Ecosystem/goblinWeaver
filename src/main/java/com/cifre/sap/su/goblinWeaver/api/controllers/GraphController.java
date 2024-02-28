package com.cifre.sap.su.goblinWeaver.api.controllers;

import com.cifre.sap.su.goblinWeaver.api.entities.ReleaseQueryList;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.edges.DependencyEdge;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.ReleaseNode;
import com.cifre.sap.su.goblinWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Tag(name = "Graph")
public class GraphController {

    @Operation(
            description = "Get the project rooted all possibilities graph",
            summary = "Get the project rooted all possibilities graph from releases dependencies list"
    )
    @PostMapping("/graph/allPossibilitiesRooted")
    public JSONObject getAllPossibilitiesRootedGraph(@RequestBody ReleaseQueryList releaseQueryList) {
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (ReleaseQueryList.Release release : releaseQueryList.getReleases()) {
            resultGraph.addEdge(new DependencyEdge("ROOT", release.getGa(), release.getVersion(), "compile"));
        }
        resultGraph.mergeGraph(
                GraphDatabaseSingleton.getInstance()
                        .getAllPossibilitiesGraph(
                                releaseQueryList.getReleases().stream().map(ReleaseQueryList.Release::getGa).collect(Collectors.toSet()
                                )
                        )
        );
        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }
}
