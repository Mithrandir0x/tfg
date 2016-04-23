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

}
