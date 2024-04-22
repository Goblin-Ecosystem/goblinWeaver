package com.cifre.sap.su.goblinWeaver.utils;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OsvProceeding {
    private static final String ROOT_PATH = ConstantProperties.osvDataFolderPath;
    private static final String DATA_PATH = ROOT_PATH+"/maven";
    public static final String AGGREGATED_DATA_FILE = ROOT_PATH+"/aggregated_data.json";
    private static final String OSV_DATA_URL = "https://storage.googleapis.com/osv-vulnerabilities/Maven/all.zip";

    public static void initOsvData(String[] args){
        boolean noUpdateFlag = false;
        boolean dataFileExist = false;
        for (String arg : args) {
            if ("noUpdate".equals(arg)) {
                noUpdateFlag = true;
                File dataFile = new File(AGGREGATED_DATA_FILE);
                dataFileExist = dataFile.exists();
                break;
            }
        }
        if(noUpdateFlag && dataFileExist){
            return;
        }
        downloadOsvDatabase();
        createAggregateDataFile();
        try {
            OsvDataSingleton.getDataJsonObject();
            GraphDatabaseSingleton.getInstance().removeAddedValuesOnGraph(Set.of(AddedValueEnum.CVE, AddedValueEnum.CVE_AGGREGATED));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    private static void createAggregateDataFile(){
        Map<String, JSONArray> aggregatedData = new HashMap<>();
        try{
            File osvFolder = new File(DATA_PATH);
            if (osvFolder.isDirectory()) {
                File[] files = osvFolder.listFiles();
                System.out.println("Prepare osv aggregated json");
                for (File file : files) {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                    JSONObject cveObject = new JSONObject();
                    JSONArray aliases = jsonObject.get("aliases") == null ? new JSONArray() : (JSONArray) jsonObject.get("aliases");
                    String cveName = aliases.size() == 0 ? "UNKNOWN" : aliases.get(0).toString();
                    JSONObject database_specific = jsonObject.get("database_specific") == null ? new JSONObject() : (JSONObject) jsonObject.get("database_specific");
                    String severity = database_specific.get("severity") == null ? "UNKNOWN" : database_specific.get("severity").toString();
                    String cwe_ids = database_specific.get("cwe_ids") == null ? "UNKNOWN" : database_specific.get("cwe_ids").toString();
                    cveObject.put("name", cveName.replaceAll("[\"]",""));
                    cveObject.put("severity", severity.replaceAll("[\"]",""));
                    cveObject.put("cwe_ids", cwe_ids.replaceAll("[\"]",""));
                    JSONArray affectedPackages = (JSONArray) jsonObject.get("affected");
                    for (Object obj : affectedPackages) {
                        JSONObject affected = (JSONObject) obj;
                        JSONObject pkg = (JSONObject) affected.get("package");
                        String packageName = (String) pkg.get("name");
                        JSONArray versions = affected.get("versions") == null ? new JSONArray() : (JSONArray) affected.get("versions");
                        for (Object versionObj : versions) {
                            String version = versionObj.toString();
                            String key = packageName + ":" + version;
                            if (!aggregatedData.containsKey(key)) {
                                aggregatedData.put(key, new JSONArray());
                            }
                            aggregatedData.get(key).add(cveObject);
                        }
                    }
                }
            }
            System.out.println("Export osv aggregated json");
            JSONObject outputObject = new JSONObject(aggregatedData);
            try (FileWriter file = new FileWriter(AGGREGATED_DATA_FILE)) {
                file.write(outputObject.toJSONString());
                file.flush();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static boolean downloadOsvDatabase(){
        System.out.println("Download osv dataset");
        // Create folder if not exist
        File dir = new File(DATA_PATH);
        File rootDir = new File(ROOT_PATH);
        if (rootDir.exists()) {
            rootDir.delete();
        }
        dir.mkdirs();
        try {
            URL url = new URL(OSV_DATA_URL);
            try (InputStream in = url.openStream();
                 ZipInputStream zipIn = new ZipInputStream(in)) {

                ZipEntry entry;
                byte[] buffer = new byte[1024];

                while ((entry = zipIn.getNextEntry()) != null) {
                    String filePath = DATA_PATH + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                            int read;
                            while ((read = zipIn.read(buffer)) != -1) {
                                bos.write(buffer, 0, read);
                            }
                        }
                    } else {
                        File subDir = new File(filePath);
                        subDir.mkdir();
                    }

                    zipIn.closeEntry();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
