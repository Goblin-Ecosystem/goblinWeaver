package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import org.neo4j.driver.util.Pair;

import java.util.Map;

public interface AddedValue<T> {
    AddedValueEnum getAddedValueEnum();
    String getNodeId();
    void setValue(String value);
    void computeValue();
    Map<String, Object> getValue();
    T stringToValue(String jsonString);
    String valueToString();
}
