package com.beabloo.bigdata.cockroach.spec;

import com.beabloo.bigdata.model.WifiPresenceLog;

public enum ActivityDefinition {

    WIFI_PRESENCE(Platform.WIFI, Event.PRESENCE, WifiPresenceLog.class);

    private Platform platform;
    private Event event;
    private Class model;

    public static ActivityDefinition getActivityDefinition(Platform platform, Event event) {
        for ( ActivityDefinition activityDefinition : values() ) {
            if ( activityDefinition.getPlatform().equals(platform) && activityDefinition.getEvent().equals(event) ) {
                return activityDefinition;
            }
        }

        return null;
    }

    ActivityDefinition(Platform platform, Event event, Class model) {
        this.platform = platform;
        this.event = event;
        this.model = model;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Class getModel() {
        return model;
    }

    public void setModel(Class model) {
        this.model = model;
    }

}
