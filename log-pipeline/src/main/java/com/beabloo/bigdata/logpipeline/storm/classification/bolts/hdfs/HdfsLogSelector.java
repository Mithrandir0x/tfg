package com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.spec.Platform;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.common.Partitioner;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HdfsLogSelector {

    private static final Logger log = LoggerFactory.getLogger(HdfsLogSelector.class);

    public static HdfsLogFormat getHdfsLogFormat() {
        return new HdfsLogFormat();
    }

    public static HdfsLogPartitioner getHdfsLogPartitioner() {
        return new HdfsLogPartitioner();
    }

    static class HdfsLogFormat implements RecordFormat {

        @Override
        public byte[] format(Tuple input) {
            CockroachLog cockroachLog = (CockroachLog) input.getValueByField("log");
            String logLine = cockroachLog.toString() + CockroachLog.linesTerminatedBy;
            return logLine.getBytes();
        }

    }

    static class HdfsLogPartitioner implements Partitioner {

        private Map<String, ActivityDefinition> activities;

        public HdfsLogPartitioner() {
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

}
