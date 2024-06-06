package com.cifre.sap.su.goblinWeaver.api.entities.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class FilterEnumDeserializer  extends StdDeserializer<FilterEnum> {
    protected FilterEnumDeserializer() {
        super(FilterEnum.class);
    }

    @Override
    public FilterEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return FilterEnum.valueOf(p.getText().toUpperCase());
    }
}
