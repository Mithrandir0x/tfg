package edu.ub.bigdata.logpipeline.storm.classification.spouts;

import org.apache.storm.kafka.KafkaSpout;

public class RawLogKafkaSpout extends KafkaSpout {

    public static final String ID = "RAWLOG_KAFKA_SPOUT_ID";

    public RawLogKafkaSpout(String zookeeperHosts) {
        super(new RawLogKafkaSpoutConfig(zookeeperHosts));
    }

}
