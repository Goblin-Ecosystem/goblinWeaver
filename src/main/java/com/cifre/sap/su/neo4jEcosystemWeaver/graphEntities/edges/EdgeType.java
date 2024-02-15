package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.edges;

public enum EdgeType {
    DEPENDENCY,
    RELATIONSHIP_AR;

    public static EdgeType neo4jTypeToEnum(String type) {
        for (EdgeType edgeType : EdgeType.values()) {
            if (edgeType.name().equalsIgnoreCase(type)) {
                return edgeType;
            }
        }
        return null;
    }
}
