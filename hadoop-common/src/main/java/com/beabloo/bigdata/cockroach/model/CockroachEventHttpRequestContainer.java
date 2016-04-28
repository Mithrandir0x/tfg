package com.beabloo.bigdata.cockroach.model;

import java.util.List;

public class CockroachEventHttpRequestContainer {

    private List<ParamsContainer> events;

    public List<ParamsContainer> getEvents() {
        return events;
    }

    public void setEvents(List<ParamsContainer> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        if ( events != null ) {
            StringBuilder stringBuilder = new StringBuilder();
            for ( ParamsContainer paramsContainer : events ) {
                stringBuilder.append(String.format(" [%s] ", paramsContainer));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

}
