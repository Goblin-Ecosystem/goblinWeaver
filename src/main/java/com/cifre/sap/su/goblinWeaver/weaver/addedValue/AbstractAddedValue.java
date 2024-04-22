package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractAddedValue<T> implements AddedValue<T>{
    protected final String nodeId;
    protected T value;

    public AbstractAddedValue(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getNodeId(){
        return nodeId;
    }

    @Override
    public T getValue(){
        return value;
    }

    @Override
    public Map<String, Object> getValueMap() {
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public void setValue(String value) {
        this.value = this.stringToValue(value);
    }
}
