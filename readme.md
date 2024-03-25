# Gobelin Weaver
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)

The Weaver is a REST API that takes a Cypher query and desired additional information as input and returns the result of this query on a Neo4j ecosystem dependency graph, enriching it on the fly according to the user’s needs.

This weaver comes in addition to the ecosystem dependency graph miner available here: https://github.com/Goblin-Ecosystem/goblinWeaver.  
An example of usage of this tool is available here: https://github.com/Goblin-Ecosystem/mavenDatasetExperiences  
A Zenodo archive that contains the associated dataset dump and the Weaver jar is available here: https://zenodo.org/records/10291589.

If you use the dataset dump present in Zenodo, please use a Neo4j version 4.x.

## Added values
- CVE: We use the osv.dev dataset to get CVEs information (https://osv.dev/).
- CVE_AGGREGATED: Aggregate release and dependencies (with transitivity) CVE.
- FRESHNESS: Corresponds, for a specific release, to the number of more recent releases available and to the time elapsed in milliseconds between the specific release and the most recent release.
- FRESHNESS_AGGREGATED: Aggregate release and dependencies (with transitivity) freshness.
- POPULARITY_1_YEAR: Corresponds, for a specific release, the number of version released within a maximum of one year after the current date using the specified release.
- POPULARITY_1_YEAR_AGGREGATED: Aggregate release and dependencies (with transitivity) POPULARITY_1_YEAR.
- SPEED: Corresponds to the average number of releases per day of an artifact. More information here: https://benevol2022.github.io/papers/DamienJaime.pdf

## Requirements
- Java 17
- Maven, with MAVEN_HOME defines
- An active Neo4j database containing the Maven Central dependency graph.

## Build
To build this project, run:
> mvn clean package

## Run
To launch the API, you must provide the URI, user and password of your Neo4J database containing the Maven Central dependency graph.  
The program will first download the osv.dev dataset and create a folder called "osvData", it's takes approximately 3m30s.  
If you already have downloaded this dataset and you don't want to update it, you can add the "noUpdate" argument on the java -jar command.

Example:
> java -Dneo4jUri="bolt://localhost:7687/" -Dneo4jUser="neo4j" -Dneo4jPassword="Password1" -jar .\target\goblinWeaver-1.0.0.jar


> java -Dneo4jUri="bolt://localhost:7687/" -Dneo4jUser="neo4j" -Dneo4jPassword="Password1" -jar .\target\goblinWeaver-1.0.0.jar

## Use the API
Pre-designed requests are available, but you can also send your own Cypher requests directly to the API.  
You can add to the body query for the API a list of Added values, and it will enrich the result for you.

A swagger documentation of the API is available here:
> http://localhost:8080/swagger-ui/index.html

## Add new added values
The Weaver is designed to be extensible, allowing a user to easily add information their research need.  
Here's how to add an added value:
1. Go to weaver/addedValue/AddedValueEnum and add the name of your new value.
2. Fill the three methods of this enumeration with your new added value
3. Create a new class that extends weaver/addedValue/AbstractAddedValue.
   3.1. If you also want to create an aggregated value of your new added value, create a new class that extends your previous new class and implements the "AggregateValue" interface.
4. Write your internal logic in this new class.

## Licensing
Copyright 2023 SAP SE or an SAP affiliate company and Neo4j Ecosystem Weaver. Please see our [LICENSE](LICENSE) for copyright and license information.
