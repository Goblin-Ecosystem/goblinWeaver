package com.cifre.sap.su.goblinWeaver.graphDatabase.neo4j;

import com.cifre.sap.su.goblinWeaver.graphDatabase.QueryDictionary;

public class Neo4jQueryDictionary implements QueryDictionary {

    @Override
    public String getSpecificArtifactQuery(String artifactId){
        return "MATCH (a:Artifact) " +
                "WHERE a.id = '" + artifactId + "' " +
                "RETURN a";
    }

    @Override
    public String getArtifactReleasesQuery(String artifactId) {
        return "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                "WHERE a.id = '" + artifactId + "' " +
                "RETURN r";
    }

    @Override
    public String getLinkedArtifactReleasesAndEdgesQuery(String artifactId) {
        return "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                "WHERE a.id = '" + artifactId + "' " +
                "RETURN a,e,r";
    }

    @Override
    public String getSpecificRelease(String releaseId) {
        return "MATCH (r:Release) " +
                "WHERE r.id = '" + releaseId + "' " +
                "RETURN r";
    }

    @Override
    public String getReleaseDependent(String artifactId, String releaseVersion) {
        return "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                "WHERE a.id = '"+artifactId+"' AND d.targetVersion = '"+releaseVersion+"' " +
                "RETURN r";
    }

    @Override
    public String getNewerReleases(String releaseId, String artifactId) {
        return "MATCH (r:Release) " +
                "WHERE r.id = '"+releaseId+"' " +
                "WITH r.timestamp as timestamp " +
                "MATCH (a:Artifact)-[e:relationship_AR]->(r:Release) " +
                "WHERE a.id = '"+artifactId+"' AND r.timestamp > timestamp " +
                "RETURN r";
    }

    @Override
    public String getReleaseFreshness(String releaseId) {
        return "MATCH (r1:Release)<-[:relationship_AR]-(:Artifact)-[:relationship_AR]->(r2:Release) " +
                "WHERE r1.id = '"+releaseId+"' AND r2.timestamp > r1.timestamp " +
                "WITH r2, r2.timestamp - r1.timestamp AS difference " +
                "RETURN count(r2) AS numberMissedRelease, max(difference) AS outdatedTimeInMs";
    }

    @Override
    public String getArtifactRhythm(String artifactId) {
        return "MATCH (a:Artifact) -[e:relationship_AR]-> (r:Release) " +
                "WHERE a.id = '"+artifactId+"' " +
                "RETURN r.timestamp AS timestamp";
    }

    @Override
    public String getReleaseDirectCompileDependencies(String artifactId) {
        return "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                "WHERE r.id = '"+artifactId+"' AND (d.scope = 'compile') " +
                "WITH a,d " +
                "MATCH (dep:Release {id: a.id+':'+d.targetVersion}) " +
                "RETURN dep";
    }

    @Override
    public String getReleaseDirectCompileDependenciesEdgeAndArtifact(String artifactId) {
        return "MATCH (r:Release)-[d:dependency]->(a:Artifact) " +
                "WHERE r.id = '"+artifactId+"' AND (d.scope = 'compile') " +
                "RETURN a,d";
    }

    @Override
    public String getReleaseNumberCount(){
        return "MATCH (r:Release) " +
                "RETURN count(r)";
    }
}
