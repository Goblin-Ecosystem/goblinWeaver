package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphEntities.nodes.NodeType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AddedValueEnumDeserializer.class)
public enum AddedValueEnum {
    CVE,
    CVE_AGGREGATED,
    FRESHNESS,
    FRESHNESS_AGGREGATED,
    POPULARITY,
    POPULARITY_AGGREGATED,
    SPEED;

    public NodeType getTargetNodeType(){
        return switch (this) {
            case CVE, CVE_AGGREGATED, FRESHNESS, FRESHNESS_AGGREGATED, POPULARITY, POPULARITY_AGGREGATED -> NodeType.RELEASE;
            case SPEED -> NodeType.ARTIFACT;
        };
    }

    public Class<? extends AddedValue<?>> getAddedValueClass(){
        return switch (this) {
            case CVE -> Cve.class;
            case CVE_AGGREGATED -> CveAggregated.class;
            case FRESHNESS -> Freshness.class;
            case FRESHNESS_AGGREGATED -> FreshnessAggregated.class;
            case POPULARITY -> Popularity.class;
            case POPULARITY_AGGREGATED -> PopularityAggregated.class;
            case SPEED -> Speed.class;
        };
    }

    public boolean isAggregatedValue(){
        return switch (this) {
            case CVE_AGGREGATED, FRESHNESS_AGGREGATED, POPULARITY_AGGREGATED -> true;
            default -> false;
        };
    }

    public String getJsonKey(){
        return this.name().toLowerCase();
    }
}
