package com.cifre.sap.su.goblinWeaver.api.entities;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Set;

public class ReleaseQueryList {
    Set<Release> releases;
    private Set<AddedValueEnum> addedValues;

    public Set<Release> getReleases() {
        return releases;
    }

    public void setReleases(Set<Release> releases) {
        this.releases = releases;
    }

    public Set<AddedValueEnum> getAddedValues() {
        return addedValues;
    }

    public void setAddedValues(Set<AddedValueEnum> addedValues) {
        this.addedValues = addedValues;
    }

    public static class Release{
        private String groupId;
        private String artifactId;
        private String version;

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

        @Hidden
        public String getGa(){
            return groupId+":"+artifactId;
        }

        @Hidden
        public String getGav(){
            return groupId+":"+artifactId+":"+version;
        }
    }
}
