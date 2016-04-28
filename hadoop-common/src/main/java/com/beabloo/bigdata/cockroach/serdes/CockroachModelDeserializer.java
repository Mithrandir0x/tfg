package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.spec.Platform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CockroachModelDeserializer {

    private static final Logger log = LoggerFactory.getLogger(CockroachModelDeserializer.class);

    private static final Pattern eventPattern = Pattern.compile("\"event\"\\: *([0-9]*)");
    private ObjectMapper objectMapper;

    private static final ValidatorFactory factory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
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
                log.info(String.format("Deserializing platform [%s] event [%s]", platform.name(), event.name()));
                values = deserializers.get(ActivityDefinition.getActivityDefinition(platform, event)).deserialize(paramsValues, extraParams);
            } else {
                log.warn(String.format("Unknown event id [%s]", eventId));
            }
        } else {
            log.warn(String.format("Unknown platform [%s]", platformName));
        }

        return values;
    }

    protected String getEventId(String paramsValues) {
        String eventId = null;

        log.debug(String.format("paramsValues [%s]", paramsValues));

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
            CockroachLog cockroachLog = (CockroachLog) objectMapper.readValue(paramsValues, activityDefinition.getModel());
            cockroachLog.setActivityDefinition(activityDefinition);

            log.info(String.format("Deserialized cockroachLog [%s]", cockroachLog));

            HashMap<String, Object> extras = objectMapper.readValue(extraParams,  new TypeReference<HashMap<String, Object>>() {});
            Map<String, String> logExtras = cockroachLog.getExtras();
            for ( Map.Entry<String, Object> entry : extras.entrySet() ) {
                logExtras.put(entry.getKey(), entry.getValue().toString());
            }

            Set<ConstraintViolation<CockroachLog>> constraintViolations = validator.validate(cockroachLog);
            if ( constraintViolations.size() > 0 ) {
                for ( ConstraintViolation<CockroachLog> constraintViolation : constraintViolations ) {
                    log.error(constraintViolation.getMessage());
                }

                return null;
            }

            return cockroachLog;
        }

    }

}
