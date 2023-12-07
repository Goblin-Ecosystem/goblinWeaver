package com.cifre.sap.su.neo4jEcosystemWeaver.api.entities;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;

import java.util.List;

public class ReleaseQuery {
    private String groupId;
    private String artifactId;
    private String version;
    private List<AddedValueEnum> addedValues;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(List<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }

    public String getGa(){
        return groupId+":"+artifactId;
    }

    @Override
    public String toString() {
        return groupId+":"+artifactId+":"+version;
    }
}
