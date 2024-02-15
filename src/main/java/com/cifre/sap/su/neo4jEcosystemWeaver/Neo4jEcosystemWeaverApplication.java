package com.cifre.sap.su.neo4jEcosystemWeaver;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.utils.OsvProceeding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Neo4jEcosystemWeaverApplication {

	public static void main(String[] args) {
		GraphDatabaseSingleton.getInstance(); // Init database connection
		OsvProceeding.initOsvData(args); // Download CVE dataset
		SpringApplication.run(Neo4jEcosystemWeaverApplication.class, args); // Run API
	}

}
