package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import java.util.Map;

public interface AddedValue<T> {
    AddedValueEnum getAddedValueEnum();
    String getNodeId();
    void setValue(String value);
    void computeValue();
    Map<String, Object> getValueMap();
    T stringToValue(String jsonString);
    String valueToString(T value);
    T getValue();
}
