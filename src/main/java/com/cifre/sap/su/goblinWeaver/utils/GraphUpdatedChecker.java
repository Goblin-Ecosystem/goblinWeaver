package com.cifre.sap.su.goblinWeaver.utils;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class GraphUpdatedChecker {
    private static final File rootFolder = new File(ConstantProperties.dataFolderPath);
    private static final File saveFile = new File(ConstantProperties.dataFolderPath+"/databaseStatus.txt");

    public static void deleteAddedValuesIfUpdated(){
        System.out.println("Check if  Database was updated");
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
            generateCountFile();
            return;
        }
        if (!saveFile.exists()) {
            generateCountFile();
            return;
        }
        if(checkIfUpdated()){
            System.out.println("Database was updated, remove added values");
            GraphDatabaseSingleton.getInstance().removeAddedValuesOnGraph(Set.of(AddedValueEnum.FRESHNESS, AddedValueEnum.FRESHNESS_AGGREGATED, AddedValueEnum.SPEED));
            generateCountFile();
        }
    }

    private static void generateCountFile() {
        InternGraph result = GraphDatabaseSingleton.getInstance().executeQuery(GraphDatabaseSingleton.getInstance().getQueryDictionary().getReleaseNumberCount());
        try (BufferedWriter writer = Files.newBufferedWriter(saveFile.toPath())) {
            writer.write(String.valueOf(result.getGraphValues().iterator().next().getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkIfUpdated(){
        try (BufferedReader reader = Files.newBufferedReader(saveFile.toPath())) {
            String fileCount = reader.readLine();
            InternGraph result = GraphDatabaseSingleton.getInstance().executeQuery(GraphDatabaseSingleton.getInstance().getQueryDictionary().getReleaseNumberCount());
            String graphCount = result.getGraphValues().iterator().next().getValue();
            if(fileCount.equals(graphCount)){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
