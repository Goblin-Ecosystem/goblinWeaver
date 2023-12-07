package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.GraphUtils;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.common.ReusedCalculation;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FreshnessAggregated implements AddedValue{
    private final String gav;

    public FreshnessAggregated(String gav) {
        this.gav = gav;
    }

    @Override
    public Pair<String, Object> getValue() {
        return new Pair<>() {
            @Override
            public String key() {
                return "freshness_aggregated";
            }

            @Override
            public Map<String, String> value() {
                return fillAggregatedFreshness();
            }
        };
    }

    private Map<String, String> fillAggregatedFreshness() {
        Map<String, String> aggregatedFreshnessMap = new HashMap<>();
        Set<String> gavToTreat = GraphUtils.getGavAndDependenciesSetWithTransitive(gav, new HashSet<>());
        int totalNumberMissedRelease = 0;
        long totalOutdatedTimeInMs = 0;
        for(String toTreat : gavToTreat){
            Map<String, String> freshnessToAdd = ReusedCalculation.getFreshnessMapFromGav(toTreat);
            totalNumberMissedRelease += Integer.parseInt(freshnessToAdd.get("numberMissedRelease"));
            totalOutdatedTimeInMs += Long.parseLong(freshnessToAdd.get("outdatedTimeInMs"));
        }
        aggregatedFreshnessMap.put("numberMissedRelease", Integer.toString(totalNumberMissedRelease));
        aggregatedFreshnessMap.put("outdatedTimeInMs", Long.toString(totalOutdatedTimeInMs));
        return aggregatedFreshnessMap;
    }
}
