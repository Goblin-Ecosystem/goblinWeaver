package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.ValueObject;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Speed extends AbstractAddedValue<Double> {

    public Speed(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum(){
        return AddedValueEnum.SPEED;
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
        InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getArtifactRhythm(nodeId));
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
