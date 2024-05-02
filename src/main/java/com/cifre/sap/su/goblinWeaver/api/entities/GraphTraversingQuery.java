package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.api.entities.enums.FilterEnum;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.util.Set;

public class GraphTraversingQuery {
    private Set<String> startReleasesGav;
    private Set<String> libToExpendsGa;
    private Set<FilterEnum> filters;
    private Set<AddedValueEnum> addedValues;

    public Set<String> getStartReleasesGav() {
        return startReleasesGav;
    }

    public void setStartReleasesGav(Set<String> startReleasesGav) {
        this.startReleasesGav = startReleasesGav;
    }

    public Set<String> getLibToExpendsGa() {
        return libToExpendsGa;
    }

    public void setLibToExpendsGa(Set<String> libToExpendsGa) {
        this.libToExpendsGa = libToExpendsGa;
    }

    public Set<FilterEnum> getFilters() {
        return filters;
    }

    public void setFilters(Set<FilterEnum> filters) {
        this.filters = filters;
    }

    public Set<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(Set<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }
}

