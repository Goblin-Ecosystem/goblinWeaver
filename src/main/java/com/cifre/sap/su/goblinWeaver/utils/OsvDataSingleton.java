package com.cifre.sap.su.goblinWeaver.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;

public class OsvDataSingleton {
    private static JSONObject dataJsonObject;

    private OsvDataSingleton() {
        // private constructor to prevent instantiation
    }

    public static JSONObject getDataJsonObject() throws IOException, ParseException {
        if (dataJsonObject == null) {
            synchronized (OsvDataSingleton.class) {
                if (dataJsonObject == null) {
                    JSONParser parser = new JSONParser();
                    dataJsonObject = (JSONObject) parser.parse(new FileReader(OsvProceeding.AGGREGATED_DATA_FILE));
                }
            }
        }
        return dataJsonObject;
    }
}
