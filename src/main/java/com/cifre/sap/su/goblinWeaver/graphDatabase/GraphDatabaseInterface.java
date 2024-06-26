package com.cifre.sap.su.goblinWeaver.graphDatabase;

import com.cifre.sap.su.goblinWeaver.api.entities.ReleaseQueryList;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValue;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GraphDatabaseInterface {
    QueryDictionary getQueryDictionary();
    InternGraph executeQuery(String query);
    InternGraph executeQueryWithParameters(String query, Map<String, Object> parameters);
    Map<String,Map<AddedValueEnum,String>> getNodeAddedValues(List<String> nodeIds, Set<AddedValueEnum> addedValues, NodeType nodeType);
    void addAddedValues(List<AddedValue<?>> computedAddedValues);
    void putOneAddedValueOnGraph(String nodeId, AddedValueEnum addedValueType, String value);
    void removeAddedValuesOnGraph(Set<AddedValueEnum> addedValuesType);
    InternGraph getRootedGraph(Set<String> artifactIdList);
    InternGraph getAllPossibilitiesGraph(Set<String> artifactIdList);
    InternGraph getDirectPossibilitiesGraph(Set<String> artifactIdList);
    InternGraph getDirectNewPossibilitiesGraph(Set<ReleaseQueryList.Release> artifactIdList);
    InternGraph getReleaseWithLibAndDependencies(String artifactId);
    InternGraph getArtifactReleasesGraph(String artifactId);
    InternGraph getArtifactSpecificReleasesGraph(String releaseId);
    InternGraph getArtifactNewReleasesGraph(String artifactId, long timestamp);
}
