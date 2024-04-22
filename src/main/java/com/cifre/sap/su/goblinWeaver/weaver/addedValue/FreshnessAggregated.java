package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import java.util.*;

public class FreshnessAggregated extends Freshness  implements AggregateValue<Map<String, String>>{

    public FreshnessAggregated(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.FRESHNESS_AGGREGATED;
    }

    @Override
    public void computeValue() {
        super.value = computeAggregatedValue(nodeId, new HashSet<>());
    }


    @Override
    public Map<String, String> mergeValue(Map<String, String> computedValue, Map<String, String> computeAggregatedValue) {
        int totalNumberMissedRelease = Integer.parseInt(computedValue.get("numberMissedRelease"));
        long totalOutdatedTimeInMs = Long.parseLong(computedValue.get("outdatedTimeInMs"));
        totalNumberMissedRelease += Integer.parseInt(computeAggregatedValue.get("numberMissedRelease"));
        totalOutdatedTimeInMs += Long.parseLong(computeAggregatedValue.get("outdatedTimeInMs"));
        Map<String, String> aggregatedFreshnessMap = new HashMap<>();
        aggregatedFreshnessMap.put("numberMissedRelease", Integer.toString(totalNumberMissedRelease));
        aggregatedFreshnessMap.put("outdatedTimeInMs", Long.toString(totalOutdatedTimeInMs));
        return aggregatedFreshnessMap;
    }

    @Override
    public Map<String, String> computeMetric(String nodeId) {
        return getFreshnessMapFromGav(nodeId);
    }

    @Override
    public Map<String, String> getZeroValue() {
        Map<String, String> emptyFreshness = new HashMap<>();
        emptyFreshness.put("numberMissedRelease", "0");
        emptyFreshness.put("outdatedTimeInMs", "0");
        return emptyFreshness;
    }
}
