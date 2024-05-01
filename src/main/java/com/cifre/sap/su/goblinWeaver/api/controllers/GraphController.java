package com.cifre.sap.su.goblinWeaver.api.controllers;

import com.cifre.sap.su.goblinWeaver.api.entities.GraphTraversingQuery;
import com.cifre.sap.su.goblinWeaver.api.entities.ReleaseQueryList;
import com.cifre.sap.su.goblinWeaver.api.entities.enums.FilterEnum;
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
            description = "",
            summary = ""
    )
    @PostMapping("/graph/traversing")
    public JSONObject traversingGraph(@RequestBody GraphTraversingQuery graphTraversingQuery) {
        // Create root node and its dependencies
        InternGraph resultGraph = new InternGraph();
        resultGraph.addNode(new ReleaseNode("ROOT", "ROOT", 0, ""));
        for (String releaseGav : graphTraversingQuery.getStartReleasesGav()) {
            String[] splitedGav = releaseGav.split(":");
            resultGraph.addEdge(new DependencyEdge("ROOT", splitedGav[0]+":"+splitedGav[1], splitedGav[2], "compile"));
        }
        Set<String> releasesToTreat = new HashSet<>(graphTraversingQuery.getStartReleasesGav());
        Set<String> librariesToExpends = new HashSet<>(graphTraversingQuery.getLibToExpendsGa());
        Set<String> visitedReleases = new HashSet<>();
        Set<String> visitedLibrary = new HashSet<>();
        Set<String> releasesToAddLibrary = new HashSet<>(graphTraversingQuery.getReleaseToHaveGav().stream().map( gav -> gav.substring(0, gav.lastIndexOf(':'))).collect(Collectors.toSet()));
        boolean expendsNewLibs = searchAndRemoveAllKeyWord(librariesToExpends);
        while(!releasesToTreat.isEmpty()) {
            // Step 1: for each release, get parent lib, release, lib dependencies, lib target release:
            // (lib:a)-[versions]->(release:a1)-[:dependency]->(lib:b)-[versions]->(release:b1)
            for(String releaseGav : new HashSet<>(releasesToTreat)) {
                InternGraph releaseGraph = GraphDatabaseSingleton.getInstance().getReleaseWithLibAndDependencies(releaseGav);
                resultGraph.mergeGraph(releaseGraph);
                visitedReleases.add(releaseGav);
                releasesToTreat.addAll(releaseGraph.getGraphNodes().stream().filter(ReleaseNode.class::isInstance).map(NodeObject::getId).collect(Collectors.toSet()));
                if(expendsNewLibs){
                    librariesToExpends.addAll(releaseGraph.getGraphNodes().stream().filter(ArtifactNode.class::isInstance).map(NodeObject::getId).collect(Collectors.toSet()));
                }
            }
            librariesToExpends.removeAll(visitedLibrary);
            // Step 2: for each libraryToExpends, get all releases
            // (lib:a)-[versions]->(release:a1)
            for(String libraryGa : librariesToExpends){
                InternGraph artifactGraph;
                if(graphTraversingQuery.getFilters().contains(FilterEnum.MORE_RECENT)) {
                    Long timestamp = resultGraph.getGraphNodes().stream()
                            .filter(ReleaseNode.class::isInstance)
                            .map(ReleaseNode.class::cast)
                            .filter(release -> release.getGa().equals(libraryGa))
                            .map(ReleaseNode::getTimestamp)
                            .min(Long::compare)
                            .orElse(null);
                    artifactGraph = (timestamp != null)
                            ? GraphDatabaseSingleton.getInstance().getArtifactNewReleasesGraph(libraryGa, timestamp)
                            : GraphDatabaseSingleton.getInstance().getArtifactReleasesGraph(libraryGa);
                } else {
                    artifactGraph = GraphDatabaseSingleton.getInstance().getArtifactReleasesGraph(libraryGa);
                }
                // Make sure to add the releases to add
                if(releasesToAddLibrary.contains(libraryGa)){
                    for(String releaseGav : graphTraversingQuery.getReleaseToHaveGav()
                            .stream().filter(gav -> libraryGa.equals(gav.substring(0, gav.lastIndexOf(':')))).collect(Collectors.toSet())) {
                        InternGraph releaseToAddGraph = GraphDatabaseSingleton.getInstance().getArtifactSpecificReleasesGraph(releaseGav);
                        resultGraph.mergeGraph(releaseToAddGraph);
                        releasesToTreat.add(releaseGav);
                    }
                }
                resultGraph.mergeGraph(artifactGraph);
                visitedLibrary.add(libraryGa);
                releasesToTreat.addAll(artifactGraph.getGraphNodes().stream().filter(ReleaseNode.class::isInstance).map(NodeObject::getId).collect(Collectors.toSet()));
            }
            releasesToTreat.removeAll(visitedReleases);
        }



        Weaver.weaveGraph(resultGraph, graphTraversingQuery.getAddedValues());
        return resultGraph.getJsonGraph();
    }

    private boolean searchAndRemoveAllKeyWord(Set<String> setString){
        boolean found = false;
        Set<String> toRemove = new HashSet<>();
        for (String str : setString) {
            if (str.equalsIgnoreCase("all")) {
                toRemove.add(str);
                found = true;
            }
        }
        setString.removeAll(toRemove);
        return found;
    }


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
        Set<String> visitedReleases = new HashSet<>();
        Set<String> releasesToTreat = directAllPossibilities.getGraphNodes().stream().filter(n -> n.getType().equals(NodeType.RELEASE)).map(NodeObject::getId).collect(Collectors.toSet());
        while (!releasesToTreat.isEmpty()){
            parameters.put("releaseIdList",releasesToTreat);
            InternGraph queryResult = GraphDatabaseSingleton.getInstance().executeQueryWithParameters(GraphDatabaseSingleton.getInstance().getQueryDictionary().getDependencyGraphFromReleaseIdListParameter(), parameters);
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
        Set<String> visitedReleases = new HashSet<>();
        Set<String> releasesToTreat = directAllPossibilities.getGraphNodes().stream().filter(n -> n.getType().equals(NodeType.RELEASE)).map(NodeObject::getId).collect(Collectors.toSet());
        while (!releasesToTreat.isEmpty()){
            parameters.put("releaseIdList",releasesToTreat);
            InternGraph queryResult = GraphDatabaseSingleton.getInstance().executeQueryWithParameters(GraphDatabaseSingleton.getInstance().getQueryDictionary().getDependencyGraphFromReleaseIdListParameter(), parameters);
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
