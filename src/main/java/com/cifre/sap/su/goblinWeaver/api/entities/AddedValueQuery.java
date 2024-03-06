package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.util.Set;

public class AddedValueQuery {
    private Set<AddedValueEnum> addedValues;

    public Set<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(Set<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }
}
