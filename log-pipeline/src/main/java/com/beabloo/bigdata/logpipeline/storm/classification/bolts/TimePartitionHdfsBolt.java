package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.common.Partitioner;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public abstract class TimePartitionHdfsBolt extends HdfsBolt {

    private static final Logger log = LoggerFactory.getLogger(TimePartitionHdfsBolt.class);

    public TimePartitionHdfsBolt() {
        this.withFsUrl("hdfs://nn.local.vm:8020")
                .withFileNameFormat(fileNameFormat)
                .withRecordFormat(new HdfsLogFormat())
                .withPartitioner(new HdfsLogPartitioner())
                .withRotationPolicy(new FileSizeRotationPolicy(2.0f, FileSizeRotationPolicy.Units.MB))
                .withSyncPolicy(new CountSyncPolicy(10));
    }

    public abstract byte[] format(Tuple tuple);

    private class HdfsLogFormat implements RecordFormat {

        @Override
        public byte[] format(Tuple input) {
            return format(input);
        }

    }

    private class HdfsLogPartitioner implements Partitioner {

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
            Long startEvent = tuple.getLongByField("timestamp");

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
