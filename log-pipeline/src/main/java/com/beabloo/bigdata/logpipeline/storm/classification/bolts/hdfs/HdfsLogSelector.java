package com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.beabloo.bigdata.cockroach.spec.Event;
import com.beabloo.bigdata.cockroach.spec.Platform;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.common.Partitioner;
import org.apache.storm.tuple.Tuple;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HdfsLogSelector {

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
            return cockroachLog.toString().getBytes();
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
            if ( activityDefinition != null ) {
                LogPartition partition = new LogPartition(startEvent);
                return String.format("%s/year=%s/month=%s/day=%s",
                        activityDefinition.name().toLowerCase(),
                        partition.year,
                        partition.month,
                        partition.day);
            }

            return "";
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
