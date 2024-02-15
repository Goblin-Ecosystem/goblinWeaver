package com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.QueryDictionary;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.GraphObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.ValueObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.DependencyEdge;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.EdgeObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.EdgeType;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges.RelationshipArEdge;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.ArtifactNode;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.ReleaseNode;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValue;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.types.TypeSystem;
import org.neo4j.driver.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Neo4jGraphDatabase implements GraphDatabaseInterface {
    private final Driver driver;
    private final QueryDictionary queryDictionary = new Neo4jQueryDictionary();

    public Neo4jGraphDatabase(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public QueryDictionary getQueryDictionary() {
        return queryDictionary;
    }

    @Override
    public InternGraph executeQuery(String query) {
        InternGraph graph = new InternGraph();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        NodeObject nodeObject = generateNode(pair.value().asNode());
                        if(nodeObject != null){
                            graph.addNode(nodeObject);
                        }
                    }
                    else if (pair.value().hasType(TypeSystem.getDefault().RELATIONSHIP())){
                        EdgeObject edgeObject = generateRelationship(pair.value().asRelationship());
                        if(edgeObject != null){
                            graph.addEdge(edgeObject);
                        }
                    }
                    else if (pair.value().hasType(TypeSystem.getDefault().PATH())) {
                        for(GraphObject graphObject : generatePath(pair.value().asPath())){
                            if (graphObject instanceof NodeObject) {
                                graph.addNode((NodeObject) graphObject);
                            } else {
                                graph.addEdge((EdgeObject) graphObject);
                            }
                        }
                    }
                    else{
                        graph.addValue(new ValueObject(pair.key(), pair.value().toString().replaceAll("[\"]","")));
                    }
                }
            }
            return graph;
        }
    }

    @Override
    public Map<String,Map<AddedValueEnum,String>> getNodeAddedValues(Set<String> nodeIds, List<AddedValueEnum> addedValues, NodeType nodeType) {
        String query = "MATCH (n:"+nodeType.enumToLabel()+")-[l:addedValues]->(a:AddedValue) WHERE n.id IN $nodeIds AND a.type IN $addedValues RETURN n.id, a";
        List<String> nodeTypeAddedValues = addedValues.stream().filter(a -> a.getTargetNodeType().equals(nodeType)).map(Enum::toString).collect(Collectors.toList());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nodeIds", nodeIds);
        parameters.put("addedValues", nodeTypeAddedValues);
        Map<String,Map<AddedValueEnum,String>> IdAndAddedValuesMap = new HashMap<>();
        try (Session session = driver.session()) {
            Result result = session.run(query, parameters);
            while (result.hasNext()) {
                Record record = result.next();
                Map<AddedValueEnum, String> innerMap = new HashMap<>();
                String nodeId = "";
                for (Pair<String, Value> pair : record.fields()) {
                    if (pair.value().hasType(TypeSystem.getDefault().NODE())){
                        innerMap.put(AddedValueEnum.valueOf(pair.value().asNode().get("type").asString()), pair.value().asNode().get("value").asString().replace("\\", ""));
                    }
                    else{
                        nodeId = pair.value().asString();
                    }
                }
                IdAndAddedValuesMap.computeIfAbsent(nodeId, k -> new HashMap<>()).putAll(innerMap);
            }
        }
        return IdAndAddedValuesMap;
    }

    @Override
    public void addAddedValues(List<AddedValue<?>> computedAddedValues){
        //Create node on thread to continue the program execution
        Thread insertionThread = new Thread(() -> {
            try (Session session = driver.session()) {
                Transaction tx = session.beginTransaction();
                int batch = 0;
                for(AddedValue addedValue : computedAddedValues){
                    batch++;
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("nodeId",addedValue.getNodeId());
                    parameters.put("addedValueType", addedValue.getAddedValueEnum().toString());
                    parameters.put("value", addedValue.valueToString(addedValue.getValue()));
                    tx.run("MATCH (r:"+addedValue.getAddedValueEnum().getTargetNodeType().enumToLabel()+" {id: $nodeId}) CREATE (r)-[l:addedValues]->(v:AddedValue {type: $addedValueType, value: $value})", parameters);
                    if(batch==10000){
                        tx.commit();
                        tx = session.beginTransaction();
                    }
                }
                tx.commit();
                tx.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        insertionThread.start();
    }

    @Override
    public void putOneAddedValueOnGraph(String nodeId, AddedValueEnum addedValueType, String value){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nodeId", nodeId);
        parameters.put("addedValueType", addedValueType.toString());
        parameters.put("value", value);

        String query = "MATCH (r:"+addedValueType.getTargetNodeType().enumToLabel()+" {id:$nodeId}) " +
                "CREATE (r)-[l:addedValues]->(v:AddedValue {type: $addedValueType, value: $value})";
        try (Session session = driver.session()) {
            session.run(query, parameters);
        }
    }

    @Override
    public void removeAddedValuesOnGraph(Set<AddedValueEnum> addedValuesType){
        StringBuilder cypherQuery = new StringBuilder();
        cypherQuery.append("MATCH (n:AddedValue) ")
                .append("WHERE n.type IN [");
        int i = 0;
        for (AddedValueEnum type : addedValuesType) {
            cypherQuery.append("'").append(type).append("'");
            if (++i < addedValuesType.size()) {
                cypherQuery.append(", ");
            }
        }
        cypherQuery.append("] ")
                .append("CALL { WITH n DETACH DELETE n } IN TRANSACTIONS OF 10000 ROWS;");
        try (Session session = driver.session()) {
            session.run(cypherQuery.toString());
        }
    }

    private static NodeObject generateNode(Node neo4jNode){
        NodeType nodeType = NodeType.neo4jLabelToEnum(neo4jNode.labels().iterator().next());
        if(nodeType != null){
            if (nodeType.equals(NodeType.ARTIFACT)){
                return new ArtifactNode(neo4jNode.elementId(), neo4jNode.get("id").asString(), neo4jNode.get("found").asBoolean());
            }
            else if (nodeType.equals(NodeType.RELEASE)){
                return new ReleaseNode(neo4jNode.elementId(), neo4jNode.get("id").asString(), neo4jNode.get("timestamp").asLong(), neo4jNode.get("version").asString());
            }
        }
        return null;
    }

    private static EdgeObject generateRelationship(Relationship neo4jRelationship){
        EdgeType edgeType = EdgeType.neo4jTypeToEnum(neo4jRelationship.type());
        if(edgeType != null) {
            if (edgeType.equals(EdgeType.DEPENDENCY)) {
                return new DependencyEdge(neo4jRelationship.startNodeElementId(), neo4jRelationship.endNodeElementId(),
                        neo4jRelationship.get("targetVersion").asString(), neo4jRelationship.get("scope").asString());
            }
            else if (edgeType.equals(EdgeType.RELATIONSHIP_AR)){
                return new RelationshipArEdge(neo4jRelationship.startNodeElementId(), neo4jRelationship.endNodeElementId());
            }
        }
        return null;
    }

    private static Set<GraphObject> generatePath(Path path){
        Set<GraphObject> resultSet = new HashSet<>();
        for (Node node : path.nodes()){
            resultSet.add(generateNode(node) != null ? generateNode(node) : null);
        }
        for (Relationship relationship : path.relationships()){
            resultSet.add(generateRelationship(relationship) != null ? generateRelationship(relationship) : null);
        }
        return resultSet;
    }
}
