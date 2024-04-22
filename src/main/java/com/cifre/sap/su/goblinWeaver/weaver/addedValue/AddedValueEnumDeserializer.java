package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class AddedValueEnumDeserializer extends StdDeserializer<AddedValueEnum> {

    protected AddedValueEnumDeserializer() {
        super(AddedValueEnum.class);
    }

    @Override
    public AddedValueEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return AddedValueEnum.valueOf(p.getText().toUpperCase());
    }
}
