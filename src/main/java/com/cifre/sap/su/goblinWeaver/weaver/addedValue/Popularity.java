package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

public class Popularity extends AbstractAddedValue<Integer>{

    public Popularity(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.POPULARITY;
    }

    @Override
    public Integer stringToValue(String jsonString) {
        return Integer.valueOf(jsonString);
    }

    @Override
    public String valueToString(Integer value) {
        return String.valueOf(value);
    }

    @Override
    public void computeValue() {

    }
}
