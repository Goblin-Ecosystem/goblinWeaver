package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import org.neo4j.driver.util.Pair;

public interface AddedValue {
    Pair<String, Object> getValue();
}
