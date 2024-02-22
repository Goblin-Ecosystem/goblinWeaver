package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.api.entities.ReleaseQueryList;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.DependencyEdge;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.ArtifactNode;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.ReleaseNode;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
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
        getAllPossibilitiesGraph(releaseQueryList.getReleases().stream().map(ReleaseQueryList.Release::getGa).collect(Collectors.toSet()), resultGraph, new HashSet<>());
        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }

    private void getAllPossibilitiesGraph(Set<String> artifactIdList, InternGraph resultGraph, Set<String> visitedGa){
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        for (String artifactId : artifactIdList) {
            if(visitedGa.contains(artifactId)){
                continue;
            }
            visitedGa.add(artifactId);
            // get artifact -[relationship_AR]-> release graph
            InternGraph artifactReleaseGraph = gdb.executeQuery(gdb.getQueryDictionary().getLinkedArtifactReleasesAndEdgesQuery(artifactId));
            resultGraph.mergeGraph(artifactReleaseGraph);
            for(String releaseNodeId : artifactReleaseGraph.getGraphNodes().stream().filter(ReleaseNode.class::isInstance)
                    .map(NodeObject::getId).collect(Collectors.toSet())){
                // get release -[dependency]-> artifact graph
                InternGraph releaseDependencyGraph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependenciesEdgeAndArtifact(releaseNodeId));
                resultGraph.mergeGraph(releaseDependencyGraph);
                getAllPossibilitiesGraph(releaseDependencyGraph.getGraphNodes().stream().filter(ArtifactNode.class::isInstance).map(NodeObject::getId).collect(Collectors.toSet())
                        , resultGraph, visitedGa);
            }
        }
    }
}
