package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.common.ReusedCalculation;
import org.neo4j.driver.util.Pair;

import java.util.Map;
import java.util.Set;

public class Cve implements AddedValue{
    private final String gav;

    public Cve(String gav) {
        this.gav = gav;
    }

    @Override
    public Pair<String, Object> getValue() {
        return new Pair<>() {
            @Override
            public String key() {
                return "CVE";
            }

            @Override
            public Set<Map<String, String>> value() {
                return fillCve();
            }
        };
    }

    private Set<Map<String, String>> fillCve(){
        return ReusedCalculation.getCveFromGav(gav);
    }
}
