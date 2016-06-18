package com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs;

import com.beabloo.bigdata.yaelp.spec.ActivityDefinition;
import org.apache.storm.hdfs.common.Partitioner;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimeHdfsPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(TimeHdfsPartitioner.class);

    private Map<String, ActivityDefinition> activities;

    public TimeHdfsPartitioner() {
        activities = new HashMap<>();
        for ( ActivityDefinition activityDefinition : ActivityDefinition.values() ) {
            activities.put(activityDefinition.name(), activityDefinition);
        }
    }

    @Override
    public String getPartitionPath(Tuple tuple) {
        String activityName = tuple.getStringByField("activity");
        Long startEvent = tuple.getLongByField("startEvent");

        ActivityDefinition activityDefinition = activities.get(activityName);
        if ( activityDefinition != null && startEvent != null ) {
            // @TODO Check if this component should assume all start events are expressed in seconds
            LogPartition partition = new LogPartition(startEvent * 1000);
            return String.format("%s/year=%d/month=%02d/day=%02d",
                    activityDefinition.name().toLowerCase(),
                    partition.year,
                    partition.month,
                    partition.day);
        } else {
            log.warn(String.format("Invalid log. activity [%s] or startEvent [%s] is null.", activityName, startEvent));
        }

        return "error";
    }

    private class LogPartition {

        public int year;
        public int month;
        public int day;

        public LogPartition(long timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

    }

}
