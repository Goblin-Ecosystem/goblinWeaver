package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Set;

public class ReleaseQuery {
    private String groupId;
    private String artifactId;
    private String version;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(Set<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }

    @Hidden
    public String getGa(){
        return groupId+":"+artifactId;
    }

    @Override
    public String toString() {
        return groupId+":"+artifactId+":"+version;
    }
}
