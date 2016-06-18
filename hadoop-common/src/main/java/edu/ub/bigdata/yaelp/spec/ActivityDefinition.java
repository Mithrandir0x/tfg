package edu.ub.bigdata.yaelp.spec;

import edu.ub.bigdata.model.WifiPresenceLog;

public enum ActivityDefinition {

    WIFI_PRESENCE(Environment.WIFI, Trigger.PRESENCE, WifiPresenceLog.class);

    private Environment environment;
    private Trigger trigger;
    private Class model;

    public static ActivityDefinition getActivityDefinition(Environment environment, Trigger trigger) {
        for ( ActivityDefinition activityDefinition : values() ) {
            if ( activityDefinition.getEnvironment().equals(environment) && activityDefinition.getTrigger().equals(trigger) ) {
                return activityDefinition;
            }
        }

        return null;
    }

    ActivityDefinition(Environment environment, Trigger trigger, Class model) {
        this.environment = environment;
        this.trigger = trigger;
        this.model = model;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Class getModel() {
        return model;
    }

    public void setModel(Class model) {
        this.model = model;
    }

}
