package com.cifre.sap.su.neo4jEcosystemWeaver.api.controllers;

import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
