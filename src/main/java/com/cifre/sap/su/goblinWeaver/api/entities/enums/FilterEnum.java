package com.cifre.sap.su.goblinWeaver.api.entities.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FilterEnumDeserializer.class)
public enum FilterEnum {
    MORE_RECENT
}
