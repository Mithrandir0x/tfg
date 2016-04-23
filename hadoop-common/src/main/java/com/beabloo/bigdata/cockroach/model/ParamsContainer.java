package com.beabloo.bigdata.cockroach.model;

public class ParamsContainer {

    private String paramsValues;
    private String extraParams;

    public ParamsContainer() {
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public String getParamsValues() {
        return paramsValues;
    }

    public void setParamsValues(String paramsValues) {
        this.paramsValues = paramsValues;
    }

    @Override
    public String toString() {
        return String.format("{ \"paramsValues\": %s, \"extraParams\": %s }", paramsValues, extraParams);
    }

}
