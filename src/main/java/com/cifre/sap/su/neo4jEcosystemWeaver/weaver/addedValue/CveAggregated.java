package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.common.ReusedCalculation;
import org.neo4j.driver.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CveAggregated implements AddedValue{
    private final String gav;

    public CveAggregated(String gav) {
        this.gav = gav;
    }

    @Override
    public Pair<String, Object> getValue() {
        return new Pair<>() {
            @Override
            public String key() {
                return "CVE_aggregated";
            }

            @Override
            public Set<Map<String, String>> value() {
                return fillAggregatedCve();
            }
        };
    }

    private Set<Map<String, String>> fillAggregatedCve(){
        Set<Map<String, String>> allCVE = new HashSet<>();
        Set<String> gavToTreat = GraphUtils.getGavAndDependenciesSetWithTransitive(gav, new HashSet<>());
        for(String toTreat : gavToTreat){
            allCVE.addAll(ReusedCalculation.getCveFromGav(toTreat));
        }
        return allCVE;
    }
}
