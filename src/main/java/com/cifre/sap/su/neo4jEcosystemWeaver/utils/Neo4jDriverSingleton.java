package com.cifre.sap.su.neo4jEcosystemWeaver.utils;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDriverSingleton {

    private static Driver driver;

    private Neo4jDriverSingleton() {
        // private constructor to prevent instantiation
    }

    public static Driver getDriverInstance() {
        if (driver == null) {
            synchronized (Neo4jDriverSingleton.class) {
                if (driver == null) {
                    String uri = System.getProperty("neo4jUri");
                    String user = System.getProperty("neo4jUser");
                    String password = System.getProperty("neo4jPassword");
                    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
                }
            }
        }
        return driver;
    }
}
