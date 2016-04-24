package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.spec.Platform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CockroachModelDeserializer {

    private static final Pattern eventPattern = Pattern.compile("\"event\"\\: ([0-9]*)");
    private ObjectMapper objectMapper;

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator = factory.getValidator();

    private Map<ActivityDefinition, ActivityDeserializer> deserializers = new HashMap<>();

    public CockroachModelDeserializer() {
        objectMapper = new ObjectMapper();

        for ( ActivityDefinition activityDefinition : ActivityDefinition.values() ) {
            deserializers.put(activityDefinition, new ActivityDeserializer(activityDefinition));
        }
    }

    public CockroachLog deserialize(String platformName, String paramsValues, String extraParams) throws Exception {
        CockroachLog values = null;

        Platform platform = Platform.getPlatform(platformName);
        if ( platform != null ) {
            String eventId = getEventId(paramsValues);
            Event event = Event.getEvent(eventId);
            if ( event != null ) {
                values = deserializers.get(ActivityDefinition.getActivityDefinition(platform, event)).deserialize(paramsValues, extraParams);
            }
        }

        return values;
    }

    private String getEventId(String paramsValues) {
        String eventId = null;

        Matcher matcher = eventPattern.matcher(paramsValues);
        if ( matcher.find() ) {
            eventId = matcher.group(1);
        }

        return eventId;
    }

    private class ActivityDeserializer {

        private ActivityDefinition activityDefinition;

        public ActivityDeserializer(ActivityDefinition activityDefinition) {
            this.activityDefinition = activityDefinition;
        }

        public CockroachLog deserialize(String paramsValues, String extraParams) throws Exception {
            CockroachLog log = (CockroachLog) objectMapper.readValue(paramsValues, activityDefinition.getModel());
            log.setActivityDefinition(activityDefinition);

            HashMap<String, Object> extras = objectMapper.readValue(extraParams,  new TypeReference<HashMap<String, Object>>() {});
            Map<String, String> logExtras = log.getExtras();
            for ( Map.Entry<String, Object> entry : extras.entrySet() ) {
                logExtras.put(entry.getKey(), entry.getValue().toString());
            }
            validator.validate(log);

            return log;
        }

    }

}
