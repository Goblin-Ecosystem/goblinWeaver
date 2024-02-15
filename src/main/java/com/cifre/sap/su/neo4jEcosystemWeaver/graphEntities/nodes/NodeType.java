package com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes;

public enum NodeType {
    RELEASE,
    ARTIFACT;

    public static NodeType neo4jLabelToEnum(String label) {
        for (NodeType nodeType : NodeType.values()) {
            if (nodeType.name().equalsIgnoreCase(label)) {
                return nodeType;
            }
        }
        return null;
    }

    public String enumToLabel(){
        String enumString = this.name();
        return enumString.substring(0, 1).toUpperCase() + enumString.substring(1).toLowerCase();
    }
}
