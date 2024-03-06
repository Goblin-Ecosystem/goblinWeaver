package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.util.Set;

public class ArtifactQuery {
    private String groupId;
    private String artifactId;
    private Set<AddedValueEnum> addedValues;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Set<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(Set<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }

    @Override
    public String toString() {
        return groupId+":"+artifactId;
    }
}
