package com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jGraphDatabase;

public class GraphDatabaseSingleton {

    private static GraphDatabaseInterface graphDatabase;

    private GraphDatabaseSingleton() {
        // private constructor to prevent instantiation
    }

    public static GraphDatabaseInterface getInstance() {
        if (graphDatabase == null) {
            synchronized (GraphDatabaseInterface.class) {
                //TODO: To be changed if you want to add a new graph database
                if (graphDatabase == null) {
                    String uri = System.getProperty("neo4jUri");
                    String user = System.getProperty("neo4jUser");
                    String password = System.getProperty("neo4jPassword");
                    graphDatabase = new Neo4jGraphDatabase(uri, user, password);
                }
            }
        }
        return graphDatabase;
    }
}
