package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import java.util.*;

public class CveAggregated extends Cve implements AggregateValue<Set<Map<String, String>>>{

    public CveAggregated(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.CVE_AGGREGATED;
    }

    @Override
    public void computeValue() {
        super.value = computeAggregatedValue(nodeId, new HashSet<>());
    }

    @Override
    public Set<Map<String, String>> mergeValue(Set<Map<String, String>> computedValue, Set<Map<String, String>> computeAggregatedValue) {
        Set<Map<String, String>> mergedSet = new HashSet<>(computedValue);
        mergedSet.addAll(computeAggregatedValue);
        return mergedSet;
    }

    @Override
    public Set<Map<String, String>> computeMetric(String nodeId) {
        return getCveFromGav(nodeId);
    }

    @Override
    public Set<Map<String, String>> getZeroValue() {
        return new HashSet<>();
    }
}
