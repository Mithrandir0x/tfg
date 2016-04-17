package com.beabloo.bigdata.cockroach.model;

import com.beabloo.bigdata.cockroach.aspects.Parameter;

import java.util.Map;

public class CockroachLog {

    @Parameter(name = "startevent")
    private Long recordCreationTime;

    private Map<String, String> extras;

    public Long getRecordCreationTime() {
        return recordCreationTime;
    }

    public void setRecordCreationTime(Long recordCreationTime) {
        this.recordCreationTime = recordCreationTime;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

}
