package com.beabloo.bigdata.cockroach.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CockroachEvent {

    private HashMap<String, String> paramsValues = new LinkedHashMap<>();
    private HashMap<String, String> extraParams= new LinkedHashMap<>();

    public HashMap<String, String> getParamsValues() {
        return paramsValues;
    }

    public void setParamsValues(HashMap<String, String> paramsValues) {
        this.paramsValues = paramsValues;
    }

    public HashMap<String, String> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(HashMap<String, String> extraParams) {
        this.extraParams = extraParams;
    }

}
