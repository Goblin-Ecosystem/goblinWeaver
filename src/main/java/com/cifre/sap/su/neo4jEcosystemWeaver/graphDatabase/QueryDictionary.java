package com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase;

public interface QueryDictionary {
    String getSpecificArtifactQuery(String artifactId);
    String getArtifactReleasesQuery(String artifactId);
    String getSpecificRelease(String releaseId);
    String getReleaseDependent(String artifactId, String releaseVersion);
    String getNewerReleases(String releaseId, String artifactId);
    String getReleaseFreshness(String releaseId);
    String getArtifactRhythm(String artifactId);
    String getReleaseDirectCompileDependencies(String artifactId);
}
