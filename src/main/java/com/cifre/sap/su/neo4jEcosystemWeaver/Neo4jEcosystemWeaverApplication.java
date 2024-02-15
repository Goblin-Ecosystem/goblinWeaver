package com.cifre.sap.su.neo4jEcosystemWeaver;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.neo4j.Neo4jDriverSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.OsvProceeding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Neo4jEcosystemWeaverApplication {

	public static void main(String[] args) {
		OsvProceeding.initOsvData(args); // Download CVE dataset
		Neo4jDriverSingleton.getDriverInstance(); // Init database connection
		SpringApplication.run(Neo4jEcosystemWeaverApplication.class, args); // Run API
	}

}
