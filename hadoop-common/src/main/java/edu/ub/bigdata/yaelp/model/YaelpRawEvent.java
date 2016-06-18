package edu.ub.bigdata.yaelp.model;

public class YaelpRawEvent {

    private String data;
    private String meta;

    public YaelpRawEvent() {
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("{ \"data\": %s, \"meta\": %s }", data, meta);
    }

}
