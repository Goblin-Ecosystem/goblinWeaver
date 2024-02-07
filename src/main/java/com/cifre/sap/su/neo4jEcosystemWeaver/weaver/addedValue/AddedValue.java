package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import org.neo4j.driver.util.Pair;

public interface AddedValue<T> {
    AddedValueEnum getAddedValueEnum();
    void setValue(String value);
    void computeValue();
    Pair<String, Object> getValue();
    T stringToValue(String jsonString);
    String valueToString(T value);
}
