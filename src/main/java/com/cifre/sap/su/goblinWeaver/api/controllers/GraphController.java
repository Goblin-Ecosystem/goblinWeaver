package com.cifre.sap.su.goblinWeaver.api.controllers;

import com.cifre.sap.su.goblinWeaver.api.entities.ReleaseQueryList;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.edges.DependencyEdge;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.ArtifactNode;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeObject;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.ReleaseNode;
import com.cifre.sap.su.goblinWeaver.weaver.Weaver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Graph")
public class GraphController {

    @Operation(
            description = "Get the project rooted graph",
            summary = "Get the project rooted all graph from releases dependencies list"
    )
    @PostMapping("/graph/rootedGraph")
    public JSONObject getRootedGraph(@RequestBody ReleaseQueryList releaseQueryList) {
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (ReleaseQueryList.Release release : releaseQueryList.getReleases()) {
            resultGraph.addEdge(new DependencyEdge("ROOT", release.getGa(), release.getVersion(), "compile"));
        }
        resultGraph.mergeGraph(
                GraphDatabaseSingleton.getInstance()
                        .getRootedGraph(
                                releaseQueryList.getReleases().stream().map(ReleaseQueryList.Release::getGav).collect(Collectors.toSet()
                                )
                        )
        );
        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }

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

    @Operation(
            description = "Get the project rooted direct dependencies possibilities graph",
            summary = "Get the project rooted direct dependencies possibilities graph from releases dependencies list"
    )
    @PostMapping("/graph/directPossibilitiesRooted")
    public JSONObject getDirectPossibilitiesRootedGraph(@RequestBody ReleaseQueryList releaseQueryList) {
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (ReleaseQueryList.Release release : releaseQueryList.getReleases()) {
            resultGraph.addEdge(new DependencyEdge("ROOT", release.getGa(), release.getVersion(), "compile"));
        }
        resultGraph.mergeGraph(
                GraphDatabaseSingleton.getInstance()
                        .getDirectPossibilitiesGraph(
                                releaseQueryList.getReleases().stream().map(ReleaseQueryList.Release::getGa).collect(Collectors.toSet()
                                )
                        )
        );
        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }

    @Operation(
            description = "Get the project rooted direct dependencies possibilities graph with transitive",
            summary = "Get the project rooted direct dependencies possibilities graph with transitive version from releases dependencies list"
    )
    @PostMapping("/graph/directPossibilitiesWithTransitiveRooted")
    public JSONObject getDirectPossibilitiesWithTransitiveRootedGraph(@RequestBody ReleaseQueryList releaseQueryList) {
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (ReleaseQueryList.Release release : releaseQueryList.getReleases()) {
            resultGraph.addEdge(new DependencyEdge("ROOT", release.getGa(), release.getVersion(), "compile"));
        }
        // Get direct all possibilities
        InternGraph directAllPossibilities = GraphDatabaseSingleton.getInstance()
                        .getDirectPossibilitiesGraph(releaseQueryList.getReleases().stream().map(ReleaseQueryList.Release::getGa).collect(Collectors.toSet()));
        resultGraph.mergeGraph(directAllPossibilities);
        // Get Releases dependencies
        Map<String, Object> parameters = new HashMap<>();
        // TODO pas de cypher ici
        String query = "MATCH (r:Release)-[d:dependency]->(a:Artifact)-[e:relationship_AR]->(r2:Release) " +
                "WHERE r.id IN $releaseIdList AND d.scope = 'compile' AND r2.version = d.targetVersion " +
                "RETURN d,a,e,r2";
        Set<String> visitedReleases = new HashSet<>();
        Set<String> releasesToTreat = directAllPossibilities.getGraphNodes().stream().filter(n -> n.getType().equals(NodeType.RELEASE)).map(NodeObject::getId).collect(Collectors.toSet());
        while (!releasesToTreat.isEmpty()){
            parameters.put("releaseIdList",releasesToTreat);
            InternGraph queryResult = GraphDatabaseSingleton.getInstance().executeQueryWithParameters(query, parameters);
            resultGraph.mergeGraph(queryResult);
            visitedReleases.addAll(releasesToTreat);
            Set<String> newReleaseToTreat = resultGraph.getGraphNodes().stream().filter(node -> node instanceof ReleaseNode).map(NodeObject::getId).collect(Collectors.toSet());
            newReleaseToTreat.removeAll(visitedReleases);
            releasesToTreat.clear();
            releasesToTreat.addAll(newReleaseToTreat);
        }

        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }

    @Operation(
            description = "Get the project rooted direct dependencies possibilities graph with transitive",
            summary = "Get the project rooted direct dependencies possibilities graph with transitive version from releases dependencies list"
    )
    @PostMapping("/graph/directNewPossibilitiesWithTransitiveRooted")
    public JSONObject getDirectNewPossibilitiesWithTransitiveRootedGraph(@RequestBody ReleaseQueryList releaseQueryList) {
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (ReleaseQueryList.Release release : releaseQueryList.getReleases()) {
            resultGraph.addEdge(new DependencyEdge("ROOT", release.getGa(), release.getVersion(), "compile"));
        }
        // Get direct all possibilities
        InternGraph directAllPossibilities = GraphDatabaseSingleton.getInstance()
                .getDirectNewPossibilitiesGraph(releaseQueryList.getReleases());
        resultGraph.mergeGraph(directAllPossibilities);
        // Get Releases dependencies
        Map<String, Object> parameters = new HashMap<>();
        // TODO pas de cypher ici
        String query = "MATCH (r:Release)-[d:dependency]->(a:Artifact)-[e:relationship_AR]->(r2:Release) " +
                "WHERE r.id IN $releaseIdList AND d.scope = 'compile' AND r2.version = d.targetVersion " +
                "RETURN d,a,e,r2";
        Set<String> visitedReleases = new HashSet<>();
        Set<String> releasesToTreat = directAllPossibilities.getGraphNodes().stream().filter(n -> n.getType().equals(NodeType.RELEASE)).map(NodeObject::getId).collect(Collectors.toSet());
        while (!releasesToTreat.isEmpty()){
            parameters.put("releaseIdList",releasesToTreat);
            InternGraph queryResult = GraphDatabaseSingleton.getInstance().executeQueryWithParameters(query, parameters);
            resultGraph.mergeGraph(queryResult);
            visitedReleases.addAll(releasesToTreat);
            Set<String> newReleaseToTreat = resultGraph.getGraphNodes().stream().filter(node -> node instanceof ReleaseNode).map(NodeObject::getId).collect(Collectors.toSet());
            newReleaseToTreat.removeAll(visitedReleases);
            releasesToTreat.clear();
            releasesToTreat.addAll(newReleaseToTreat);
        }

        Weaver.weaveGraph(resultGraph, releaseQueryList.getAddedValues());
        return resultGraph.getJsonGraph();
    }
}
