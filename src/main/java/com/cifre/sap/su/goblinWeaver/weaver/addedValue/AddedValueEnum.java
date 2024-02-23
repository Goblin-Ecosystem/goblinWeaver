package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeType;

public enum AddedValueEnum {
    CVE,
    CVE_AGGREGATED,
    FRESHNESS,
    FRESHNESS_AGGREGATED,
    SPEED;

    public NodeType getTargetNodeType(){
        return switch (this.name()) {
            case "CVE", "CVE_AGGREGATED", "FRESHNESS", "FRESHNESS_AGGREGATED" -> NodeType.RELEASE;
            case "SPEED" -> NodeType.ARTIFACT;
            default -> null;
        };
    }

    public Class<? extends AddedValue<?>> getAddedValueClass(){
        return switch (this.name()) {
            case "CVE" -> Cve.class;
            case "CVE_AGGREGATED" -> CveAggregated.class;
            case "FRESHNESS" -> Freshness.class;
            case "FRESHNESS_AGGREGATED" -> FreshnessAggregated.class;
            case "SPEED" -> Speed.class;
            default -> null;
        };
    }

    public boolean isAggregatedValue(){
        return switch (this.name()) {
            case "CVE", "FRESHNESS", "SPEED" -> false;
            case "CVE_AGGREGATED", "FRESHNESS_AGGREGATED" -> true;
            default -> false;
        };
    }

    public String getJsonKey(){
        return this.name().toLowerCase();
    }
}
