package com.beabloo.bigdata.yaelp.serdes;

import com.beabloo.bigdata.yaelp.model.YaelpLog;
import com.beabloo.bigdata.yaelp.spec.ActivityDefinition;
import com.beabloo.bigdata.yaelp.spec.Environment;
import com.beabloo.bigdata.yaelp.spec.Trigger;
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

public class YaelpModelDeserializer {

    private static final Logger log = LoggerFactory.getLogger(YaelpModelDeserializer.class);

    private static final Pattern triggerPattern = Pattern.compile("\"trigger\"\\: *([0-9]*)");
    private ObjectMapper objectMapper;

    private static final ValidatorFactory factory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
    private Validator validator = factory.getValidator();

    private Map<ActivityDefinition, ActivityDeserializer> deserializers = new HashMap<>();

    public YaelpModelDeserializer() {
        objectMapper = new ObjectMapper();

        for ( ActivityDefinition activityDefinition : ActivityDefinition.values() ) {
            deserializers.put(activityDefinition, new ActivityDeserializer(activityDefinition));
        }
    }

    public YaelpLog deserialize(String platformName, String paramsValues, String extraParams) throws Exception {
        YaelpLog values = null;

        Environment environment = Environment.getEnvironment(platformName);
        if ( environment != null ) {
            String eventId = getEventId(paramsValues);
            Trigger trigger = Trigger.getTrigger(eventId);
            if ( trigger != null ) {
                log.info(String.format("Deserializing environment [%s] trigger [%s]", environment.name(), trigger.name()));
                ActivityDefinition activityDefinition = ActivityDefinition.getActivityDefinition(environment, trigger);
                if ( activityDefinition != null ) {
                    values = deserializers.get(activityDefinition).deserialize(paramsValues, extraParams);
                } else {
                    // @TODO Throw specific exception
                    log.warn(String.format("Unknown activity definition. Environment [%s] Trigger [%s]", platformName, eventId));
                }
            } else {
                // @TODO Throw specific exception
                log.warn(String.format("Unknown trigger id [%s]", eventId));
            }
        } else {
            // @TODO Throw specific exception
            log.warn(String.format("Unknown environment [%s]", platformName));
        }

        return values;
    }

    protected String getEventId(String paramsValues) {
        String eventId = null;

        log.debug(String.format("data [%s]", paramsValues));

        Matcher matcher = triggerPattern.matcher(paramsValues);
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

        public YaelpLog deserialize(String paramsValues, String extraParams) throws Exception {
            YaelpLog yaelpLog = (YaelpLog) objectMapper.readValue(paramsValues, activityDefinition.getModel());
            yaelpLog.setActivityDefinition(activityDefinition);

            HashMap<String, Object> extras = objectMapper.readValue(extraParams,  new TypeReference<HashMap<String, Object>>() {});
            Map<String, String> logExtras = yaelpLog.getExtras();
            for ( Map.Entry<String, Object> entry : extras.entrySet() ) {
                logExtras.put(entry.getKey(), entry.getValue().toString());
            }

            Set<ConstraintViolation<YaelpLog>> constraintViolations = validator.validate(yaelpLog);
            log.info(String.format("Deserialized yaelpLog [%s] with [%s] constraint violations", yaelpLog, constraintViolations.size()));

            if ( constraintViolations.size() > 0 ) {
                for ( ConstraintViolation<YaelpLog> constraintViolation : constraintViolations ) {
                    log.error(constraintViolation.getMessage());
                }

                // @TODO Throw specific exception
                return null;
            }

            return yaelpLog;
        }

    }

}
