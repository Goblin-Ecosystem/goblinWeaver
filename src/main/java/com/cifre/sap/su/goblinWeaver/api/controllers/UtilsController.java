package com.cifre.sap.su.goblinWeaver.api.controllers;

import com.cifre.sap.su.goblinWeaver.api.entities.AddedValueQuery;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.AddedValueEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;


@RestController
@Tag(name = "Utils")
public class UtilsController {

    @Operation(
            description = "Get the list of existing added values on the Weaver",
            summary = "Get existing added values"
    )
    @GetMapping("/addedValues")
    public AddedValueEnum[] getAddedValue() {
        return AddedValueEnum.values();
    }

    @Operation(
            description = "Delete all added values nodes on database",
            summary = "Delete all added values"
    )
    @DeleteMapping("/addedValues")
    public void deleteAddedValues(){
        GraphDatabaseSingleton.getInstance().removeAddedValuesOnGraph(EnumSet.allOf(AddedValueEnum.class));
    }

    @Operation(
            description = "Delete specific added values nodes on database",
            summary = "Delete specific added values"
    )
    @DeleteMapping("/addedValue")
    public void deleteAddedValue(@RequestBody AddedValueQuery addedValueQuery){
        GraphDatabaseSingleton.getInstance().removeAddedValuesOnGraph(addedValueQuery.getAddedValues());
    }
}
