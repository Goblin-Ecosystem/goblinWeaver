package com.cifre.sap.su.neo4jEcosystemWeaver.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiUtils {

    public static JSONObject callWithData(String urlString, String method, String data){
        try {
            URL url = new URL(urlString);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            if(http.getResponseCode() == 200){
                JSONParser jsonParser = new JSONParser();
                return (JSONObject)jsonParser.parse(
                        new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
            }
            http.disconnect();
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.out.println("Unable to connect to API:\n" + e);
        }
        return null;
    }
}
