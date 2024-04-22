package com.cifre.sap.su.goblinWeaver.api.controllers;

import com.cifre.sap.su.goblinWeaver.api.entities.ReleaseQuery;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Releases")
public class ReleaseController {

    @Operation(
            description = "Get a specific release from groupId:ArtifactId:Version with added values",
            summary = "Get a specific release from GAV"
    )
    @PostMapping("/release")
    public JSONObject getSpecificRelease(@RequestBody ReleaseQuery releaseQuery) {
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getSpecificRelease(releaseQuery.toString()));
        Weaver.weaveGraph(graph, releaseQuery.getAddedValues());
        return graph.getJsonGraph();
    }

    @Operation(
            description = "Get newer versions of a release from groupId:ArtifactId:Version with added values",
            summary = "Get newer versions of a release from GAV"
    )
    @PostMapping("/release/newVersions")
    public JSONObject getNewerReleases(@RequestBody ReleaseQuery releaseQuery) {
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getNewerReleases(releaseQuery.toString(), releaseQuery.getGa()));
        Weaver.weaveGraph(graph, releaseQuery.getAddedValues());
        return graph.getJsonGraph();
    }

    @Operation(
            description = "Get dependents of a release from groupId:ArtifactId:Version with added values",
            summary = "Get release dependents from GAV"
    )
    @PostMapping("/release/dependents")
    public JSONObject getReleaseDependent(@RequestBody ReleaseQuery releaseQuery) {
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDependent(releaseQuery.getGa(), releaseQuery.getVersion()));
        Weaver.weaveGraph(graph, releaseQuery.getAddedValues());
        return graph.getJsonGraph();
    }
}
