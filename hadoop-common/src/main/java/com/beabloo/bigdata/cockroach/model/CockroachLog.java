package com.beabloo.bigdata.cockroach.model;

import com.beabloo.bigdata.cockroach.annotations.Parameter;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CockroachLog implements Serializable {

    public static final String fieldsTerminatedBy = "\u0001";
    public static final String collectionItemsTerminatedBy = "\u0002";
    public static final String mapKeysTerminatedBy = "\u0003";
    public static final String linesTerminatedBy = "\n";

    private ActivityDefinition activityDefinition;

    @Parameter
    @NotNull(message = "[organization] cannot be null")
    private Long organization;

    @Parameter
    @NotNull(message = "[startEvent] cannot be null")
    private Long startEvent;

    @Parameter
    @NotNull(message = "[tags] cannot be null")
    private String tags;

    @Parameter(required = false)
    private Map<String, String> extras = new HashMap<>();

    public ActivityDefinition getActivityDefinition() {
        return activityDefinition;
    }

    public void setActivityDefinition(ActivityDefinition activityDefinition) {
        this.activityDefinition = activityDefinition;
    }

    public Long getOrganization() {
        return organization;
    }

    public void setOrganization(Long organization) {
        this.organization = organization;
    }

    public Long getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Long startEvent) {
        this.startEvent = startEvent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    protected static String convertHashMapToHadoopMapInStringFormat(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder("");
        boolean first = true;

        if (map != null) {
            for (String key : map.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(collectionItemsTerminatedBy);
                }
                sb.append(key).append(mapKeysTerminatedBy).append(map.get(key));
            }
        }
        return sb.toString();
    }

}
