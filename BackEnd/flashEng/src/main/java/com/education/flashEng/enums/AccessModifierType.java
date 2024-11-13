package com.education.flashEng.enums;

import java.util.Map;
import java.util.TreeMap;

public enum AccessModifierType {
    PUBLIC("Public"),
    PRIVATE("Private"),
    CLASS("Class");

    private String value;

    AccessModifierType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Map<String,String> type(){
        Map<String,String> listType = new TreeMap<>();
        for(AccessModifierType item : AccessModifierType.values()){
            listType.put(item.toString() , item.value);
        }
        return listType;
    }

    public static String getKeyfromValue(String value) {
        for (AccessModifierType item : AccessModifierType.values()) {
            if (item.value.equals(value)) {
                return item.toString();
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
