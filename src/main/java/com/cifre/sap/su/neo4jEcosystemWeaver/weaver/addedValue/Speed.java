package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.utils.Neo4jDriverSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Speed implements AddedValue {
    private final String ga;

    public Speed(String ga) {
        this.ga = ga;
    }

    @Override
    public Pair<String, Object> getValue() {
        return new Pair<>() {
            @Override
            public String key() {
                return "speed";
            }

            @Override
            public Double value() {
                return fillSpeed();
            }
        };
    }

    private double fillSpeed(){
        String query = "MATCH (a:Artifact) -[e:relationship_AR]-> (r:Release) " +
                "WHERE a.id = '"+ga+"' " +
                "RETURN r.timestamp AS timestamp";
        // Get all artifact Releases timestamp
        Driver driver = Neo4jDriverSingleton.getDriverInstance();
        TreeSet<Long> releasesTimeStampSet = new TreeSet<>();
        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                for (Pair<String, Value> pair : record.fields()) {
                    releasesTimeStampSet.add(Long.parseLong(pair.value().toString()));
                }
            }
        }
        if(releasesTimeStampSet.size() < 2){
            return 0;
        }
        // Calculate average speed by days
        Instant minTimestamp = Instant.ofEpochMilli(releasesTimeStampSet.first());
        Instant maxTimestamp = Instant.ofEpochMilli(releasesTimeStampSet.last());
        long durationDays = Duration.between(minTimestamp, maxTimestamp).toDays();
        if (durationDays == 0) {
            return 0;
        } else {
            return (double) releasesTimeStampSet.size() / durationDays;
        }
    }

}
