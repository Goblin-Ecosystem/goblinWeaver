package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import java.util.HashSet;

public class Popularity1YearAggregated extends Popularity1Year implements AggregateValue<Integer>{

    public Popularity1YearAggregated(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.POPULARITY_1_YEAR_AGGREGATED;
    }

    @Override
    public void computeValue() {
        super.value = computeAggregatedValue(nodeId, new HashSet<>());
    }

    @Override
    public Integer mergeValue(Integer computedValue, Integer computeAggregatedValue) {
        return computedValue + computeAggregatedValue;
    }

    @Override
    public Integer computeMetric(String nodeId) {
        return super.fillPopularity1Year(nodeId);
    }

    @Override
    public Integer getZeroValue() {
        return 0;
    }
}
