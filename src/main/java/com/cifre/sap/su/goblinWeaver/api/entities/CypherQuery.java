package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.util.List;

public class CypherQuery {
    private String query;
    private List<AddedValueEnum> addedValues;
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(List<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }
}

