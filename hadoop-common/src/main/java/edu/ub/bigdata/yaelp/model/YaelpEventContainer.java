package edu.ub.bigdata.yaelp.model;

import java.util.List;

public class YaelpEventContainer {

    private List<YaelpRawEvent> events;

    public List<YaelpRawEvent> getEvents() {
        return events;
    }

    public void setEvents(List<YaelpRawEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        if ( events != null ) {
            StringBuilder stringBuilder = new StringBuilder();
            for ( YaelpRawEvent yaelpRawEvent : events ) {
                stringBuilder.append(String.format(" [%s] ", yaelpRawEvent));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

}
