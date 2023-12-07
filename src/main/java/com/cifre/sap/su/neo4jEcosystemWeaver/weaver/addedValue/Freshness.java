package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.common.ReusedCalculation;
import org.neo4j.driver.util.Pair;

import java.util.Map;

public class Freshness implements AddedValue{
    private final String gav;

    public Freshness(String gav) {
        this.gav = gav;
    }

    @Override
    public Pair<String, Object> getValue() {
        return new Pair<>() {
            @Override
            public String key() {
                return "freshness";
            }

            @Override
            public Map<String, String> value() {
                return fillFreshness();
            }
        };
    }

    private Map<String, String> fillFreshness() {
        return ReusedCalculation.getFreshnessMapFromGav(gav);
    }
}
