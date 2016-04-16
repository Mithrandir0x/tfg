package com.beabloo.bigdata.model;

public class RawLog {

    private long timestamp;
    private String type;
    private String data;

    public RawLog() {
    }

    public RawLog(long timestamp, String type, String data) {
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}