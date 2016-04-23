package com.beabloo.bigdata.logpipeline.storm.classification.spouts;

import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;

import java.util.UUID;

public class RawLogKafkaSpout extends KafkaSpout {

    public static final String ID = "RAWLOG_KAFKA_SPOUT_ID";

    public RawLogKafkaSpout() {
        super(new RawLogKafkaSpoutConfig());
    }

}
