package com.cifre.sap.su.goblinWeaver.graphDatabase;

public interface QueryDictionary {
    String getSpecificArtifactQuery(String artifactId);
    String getArtifactReleasesQuery(String artifactId);
    String getSpecificRelease(String releaseId);
    String getReleaseDependent(String artifactId, String releaseVersion);
    String getNewerReleases(String releaseId, String artifactId);
    String getReleaseFreshness(String releaseId);
    String getReleasePopularity1Year(String artifactGa, String releaseVersion);
    String getArtifactRhythm(String artifactId);
    String getReleaseDirectCompileDependencies(String artifactId);
    String getLinkedArtifactReleasesAndEdgesQuery(String artifactId);
    String getReleaseDirectCompileDependenciesEdgeAndArtifact(String artifactId);
    String getLastReleaseTimestamp();
}
