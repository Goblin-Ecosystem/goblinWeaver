package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.ValueObject;

import java.util.Iterator;

public class Popularity1Year extends AbstractAddedValue<Integer>{

    public Popularity1Year(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.POPULARITY_1_YEAR;
    }

    @Override
    public Integer stringToValue(String jsonString) {
        return Integer.valueOf(jsonString);
    }

    @Override
    public String valueToString(Integer value) {
        return String.valueOf(value);
    }

    @Override
    public void computeValue() {
        super.value = fillPopularity1Year(nodeId);

    }

    protected int fillPopularity1Year(String gav){
        int popularity = 0;
        String[] splitedGav = gav.split(":");
        if (splitedGav.length == 3) {
            String artifactGa = splitedGav[0]+":"+splitedGav[1];
            String releaseVersion = splitedGav[2];
            GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleasePopularity1Year(artifactGa, releaseVersion));
            Iterator<ValueObject> valueIterator = graph.getGraphValues().iterator();
            if(valueIterator.hasNext()) {
                popularity = Integer.parseInt(graph.getGraphValues().iterator().next().getValue());
            }
        }
        return popularity;
    }
}
