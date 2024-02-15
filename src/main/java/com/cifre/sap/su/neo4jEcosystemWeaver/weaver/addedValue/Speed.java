package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.ValueObject;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Speed implements AddedValue<Double> {
    private final String ga;
    private Double value;

    public Speed(String ga) {
        this.ga = ga;
    }

    public AddedValueEnum getAddedValueEnum(){
        return AddedValueEnum.SPEED;
    }

    public String getNodeId(){
        return ga;
    }

    @Override
    public Map<String, Object> getValueMap() {
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public Double getValue(){
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = this.stringToValue(value);
    }

    @Override
    public void computeValue() {
        this.value = fillSpeed();
    }

    @Override
    public Double stringToValue(String jsonString) {
        return Double.valueOf(jsonString);
    }

    @Override
    public String valueToString(Double value) {
        return String.valueOf(value);
    }

    private double fillSpeed(){
        TreeSet<Long> releasesTimeStampSet = new TreeSet<>();
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getArtifactRhythm(ga));
        for(ValueObject value : graph.getGraphValues()){
            releasesTimeStampSet.add(Long.parseLong(value.getValue()));
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
